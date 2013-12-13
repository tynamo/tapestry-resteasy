package org.tynamo.resteasy;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.CheckForUpdatesFilter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.IntermediateType;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.ioc.util.TimeInterval;
import org.apache.tapestry5.services.*;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.*;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ResteasyRequestFilter implements HttpServletRequestFilter, HttpRequestFactory, HttpResponseFactory {

	private final ServletContainerDispatcher servletContainerDispatcher;
	private final Dispatcher dispatcher;
	private final ResteasyProviderFactory providerFactory;
	private final ApplicationGlobals globals;

	private final Pattern filterPattern;
	private final Logger logger;

	boolean productionMode;

	private CheckForUpdatesFilter checkForUpdatesFilter;

	private RequestHandler dummyHandler = new RequestHandler() {
		@Override
		public boolean service(Request request, Response response) throws IOException {
			return false;
		}
	};

	public ResteasyRequestFilter(
			@Inject @Symbol(ResteasySymbols.MAPPING_PREFIX) String filterPath,
			Logger logger,
			ApplicationGlobals globals,
			Application application,
			SymbolSource source,
			@Symbol(SymbolConstants.PRODUCTION_MODE) boolean productionMode,
			UpdateListenerHub updateListenerHub,
			@Symbol(SymbolConstants.FILE_CHECK_INTERVAL) @IntermediateType(TimeInterval.class) long checkInterval,
			@Symbol(SymbolConstants.FILE_CHECK_UPDATE_TIMEOUT) @IntermediateType(TimeInterval.class) long updateTimeout
	) throws ServletException {

		this.logger = logger;
		this.filterPattern = Pattern.compile(filterPath + ".*", Pattern.CASE_INSENSITIVE);
		this.globals = globals;

		ListenerBootstrap bootstrap = new TapestryResteasyBootstrap(globals.getServletContext(), source);

		servletContainerDispatcher = new ServletContainerDispatcher();
		servletContainerDispatcher.init(globals.getServletContext(), bootstrap, this, this);
		dispatcher = servletContainerDispatcher.getDispatcher();
		providerFactory = servletContainerDispatcher.getDispatcher().getProviderFactory();
		processApplication(application);

		this.productionMode = productionMode;
		checkForUpdatesFilter = new CheckForUpdatesFilter(updateListenerHub, checkInterval, updateTimeout);
	}

	@Override
	public boolean service(HttpServletRequest request, HttpServletResponse response,
	                       HttpServletRequestHandler handler) throws IOException {

		String path = request.getServletPath();
		String pathInfo = request.getPathInfo();

		if (pathInfo != null) path += pathInfo;

		if (filterPattern.matcher(path).matches()) {

			if (!productionMode) {
				checkForUpdatesFilter.service(null, null, dummyHandler);
			}

			servletContainerDispatcher.service(request.getMethod(), request, response, true);
			return true;
		}

		return handler.service(request, response);
	}

	@Override
	public HttpRequest createResteasyHttpRequest(String httpMethod, HttpServletRequest request,
												 ResteasyHttpHeaders headers, ResteasyUriInfo uriInfo,
												 HttpResponse theResponse, HttpServletResponse response) {
		return new HttpServletInputMessage(request, response, globals.getServletContext(), theResponse, headers, uriInfo, httpMethod.toUpperCase(), (SynchronousDispatcher) dispatcher);
	}

	@Override
	public HttpResponse createResteasyHttpResponse(HttpServletResponse response) {
		return createServletResponse(response);
	}

	protected HttpResponse createServletResponse(HttpServletResponse response) {
		return new HttpServletResponseWrapper(response, providerFactory);
	}

	private void processApplication(Application config) {
		logger.info("Deploying " + Application.class.getName() + ": " + config.getClass());
		ArrayList<Class> actualResourceClasses = new ArrayList<Class>();
		ArrayList<Class> actualProviderClasses = new ArrayList<Class>();
		ArrayList resources = new ArrayList();
		ArrayList providers = new ArrayList();
		if (config.getClasses() != null) {
			for (Class clazz : config.getClasses()) {
				if (GetRestful.isRootResource(clazz)) {
					actualResourceClasses.add(clazz);
				} else if (clazz.isAnnotationPresent(Provider.class)) {
					actualProviderClasses.add(clazz);
				} else {
					throw new RuntimeException("Application.getClasses() returned unknown class type: " + clazz.getName());
				}
			}
		}
		if (config.getSingletons() != null) {
			for (Object obj : config.getSingletons()) {
				if (GetRestful.isRootResource(obj.getClass())) {
					logger.info("Adding singleton resource " + obj.getClass().getName() + " from Application " + Application.class.getName());
					resources.add(obj);
				} else if (obj.getClass().isAnnotationPresent(Provider.class)) {
					providers.add(obj);
				} else {
					throw new RuntimeException("Application.getSingletons() returned unknown class type: " + obj.getClass().getName());
				}
			}
		}
		for (Class clazz : actualProviderClasses) providerFactory.registerProvider(clazz);
		for (Object obj : providers) providerFactory.registerProviderInstance(obj);
		for (Class clazz : actualResourceClasses) dispatcher.getRegistry().addPerRequestResource(clazz);
		for (Object obj : resources) dispatcher.getRegistry().addSingletonResource(obj);
	}
}
