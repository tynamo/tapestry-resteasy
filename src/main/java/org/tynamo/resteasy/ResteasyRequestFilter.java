package org.tynamo.resteasy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.commons.util.TimeInterval;
import org.apache.tapestry5.http.services.ApplicationGlobals;
import org.apache.tapestry5.http.services.HttpServletRequestFilter;
import org.apache.tapestry5.http.services.HttpServletRequestHandler;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.http.services.RequestHandler;
import org.apache.tapestry5.http.services.Response;
import org.apache.tapestry5.internal.services.CheckForUpdatesFilter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.IntermediateType;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.ioc.services.UpdateListenerHub;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpRequestFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpResponseFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletInputMessage;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;

public class ResteasyRequestFilter implements HttpServletRequestFilter, HttpRequestFactory, HttpResponseFactory {

	private final ServletContainerDispatcher servletContainerDispatcher;
	private final Dispatcher dispatcher;
	private final ResteasyProviderFactory providerFactory;
	private final ApplicationGlobals globals;

	private final Pattern filterPattern;
	private final Logger logger;

	private boolean productionMode;
	private boolean corsEnabled;

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
			@Symbol(SymbolConstants.FILE_CHECK_UPDATE_TIMEOUT) @IntermediateType(TimeInterval.class) long updateTimeout,
			@Symbol(ResteasySymbols.CORS_ENABLED) boolean corsEnabled
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
		this.corsEnabled = corsEnabled;
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
			if (corsEnabled)
			{
				response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
			}
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
	public HttpResponse createResteasyHttpResponse(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest)
	{
		return createServletResponse(httpServletResponse, httpServletRequest);
	}

	protected HttpResponse createServletResponse(HttpServletResponse response, HttpServletRequest request) {
		return new HttpServletResponseWrapper(response, request, providerFactory);
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
