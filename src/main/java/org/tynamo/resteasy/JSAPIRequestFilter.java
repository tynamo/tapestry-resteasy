package org.tynamo.resteasy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.tapestry5.http.services.ApplicationGlobals;
import org.apache.tapestry5.http.services.HttpServletRequestFilter;
import org.apache.tapestry5.http.services.HttpServletRequestHandler;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.jsapi.JSAPIWriter;
import org.jboss.resteasy.jsapi.ServiceRegistry;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;

public class JSAPIRequestFilter implements HttpServletRequestFilter
{

	private final Logger logger;
	private final Pattern filterPattern;
	private final JSAPIWriter apiWriter = new JSAPIWriter();
	private Map<String, ServiceRegistry> services;

	public JSAPIRequestFilter(@Inject @Symbol(ResteasySymbols.MAPPING_PREFIX_JSAPI) String filterPath,
	                          ApplicationGlobals globals, Logger logger)
			throws Exception
	{
		this.logger = logger;

		this.filterPattern = Pattern.compile(filterPath + ".*", Pattern.CASE_INSENSITIVE);

		Map<String, ResteasyDeployment> deployments = (Map<String, ResteasyDeployment>) globals.getServletContext().getAttribute(ResteasyContextParameters.RESTEASY_DEPLOYMENTS);

		if (deployments != null)
		{
			services = new HashMap<String, ServiceRegistry>();
			for (Map.Entry<String, ResteasyDeployment> entry : deployments.entrySet())
			{
				ResourceMethodRegistry registry = (ResourceMethodRegistry) entry.getValue().getRegistry();
				ResteasyProviderFactory providerFactory = entry.getValue().getProviderFactory();
				ServiceRegistry service = new ServiceRegistry(null, registry, providerFactory, null);
				services.put(entry.getKey(), service);
			}
		}
	}

	@Override
	public boolean service(HttpServletRequest request, HttpServletResponse response, HttpServletRequestHandler handler) throws IOException
	{
		String path = request.getServletPath();
		String pathInfo = request.getPathInfo();
		if (pathInfo != null)
			path += pathInfo;

		if (filterPattern.matcher(path).matches())
		{
			String uri = request.getRequestURL().toString();
			uri = uri.substring(0, uri.length() - request.getServletPath().length());
			if (logger.isDebugEnabled())
			{
				logger.debug("Serving " + pathInfo);
				logger.debug("Query " + request.getQueryString());
			}
			if (this.services == null)
			{
				response.sendError(503, "There are no Resteasy deployments.");
			}
			response.setContentType("text/javascript");
			this.apiWriter.writeJavaScript(uri, request, response, services);
			return true;
		}
		return handler.service(request, response);
	}
}
