package org.tynamo.resteasy;


import java.util.Collection;

import jakarta.ws.rs.ext.Provider;

import org.apache.tapestry5.commons.Configuration;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.commons.ObjectLocator;
import org.apache.tapestry5.commons.OrderedConfiguration;
import org.apache.tapestry5.http.services.HttpServletRequestFilter;
import org.apache.tapestry5.http.services.HttpServletRequestHandler;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ClassNameLocator;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;

/**
 * This module is automatically included as part of the Tapestry IoC Registry
 */
public class ResteasyModule
{

	public static void bind(ServiceBinder binder)
	{
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		binder.bind(jakarta.ws.rs.core.Application.class, org.tynamo.resteasy.Application.class);
		binder.bind(HttpServletRequestFilter.class, ResteasyRequestFilter.class).withId("ResteasyRequestFilter");
		binder.bind(HttpServletRequestFilter.class, JSAPIRequestFilter.class).withId("JSAPIRequestFilter");
	}

	@Contribute(HttpServletRequestHandler.class)
	public static void httpServletRequestHandler(OrderedConfiguration<HttpServletRequestFilter> configuration,
	                                             @InjectService("ResteasyRequestFilter") HttpServletRequestFilter resteasyRequestFilter,
	                                             @InjectService("JSAPIRequestFilter") HttpServletRequestFilter jsapiRequestFilter)
	{
		configuration.add("ResteasyRequestFilter", resteasyRequestFilter, "after:IgnoredPaths", "before:GZIP");
		configuration.add("JSAPIRequestFilter", jsapiRequestFilter, "after:ResteasyRequestFilter");
	}

	@Contribute(SymbolProvider.class)
	@FactoryDefaults
	public static void setupSymbols(MappedConfiguration<String, Object> configuration)
	{
		configuration.add(ResteasySymbols.MAPPING_PREFIX, "/rest");
		configuration.add(ResteasySymbols.MAPPING_PREFIX_JSAPI, "/jsapi");
		configuration.add(ResteasySymbols.AUTOSCAN_REST_PACKAGE, true);
		configuration.add(ResteasySymbols.CORS_ENABLED, false);
	}

	@Contribute(jakarta.ws.rs.core.Application.class)
	public static void javaxWsRsCoreApplication(Configuration<Object> singletons,
	                                            ObjectLocator locator,
	                                            ResteasyPackageManager resteasyPackageManager,
	                                            ClassNameLocator classNameLocator,
	                                            Logger logger)
	{
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		for (String packageName : resteasyPackageManager.getPackageNames())
		{
			for (String className : classNameLocator.locateClassNames(packageName))
			{
				try
				{
					Class clazz = contextClassLoader.loadClass(className);
					Class rootResourceClass = GetRestful.getRootResourceClass(clazz);

					if (rootResourceClass != null)
					{
						if (rootResourceClass.equals(clazz))
						{
							if (!clazz.isInterface())
							{
								singletons.add(locator.autobuild(clazz));
							}
						} else
						{
							try
							{
								singletons.add(locator.getService(rootResourceClass));
							} catch (RuntimeException e)
							{
								logger.info(e.getMessage());
								logger.info("Trying to create a proxy for " + rootResourceClass.getName());
								singletons.add(locator.proxy(rootResourceClass, clazz));
							}
						}
					} else if (clazz.isAnnotationPresent(Provider.class))
					{
						singletons.add(locator.autobuild(clazz));
					}

				} catch (ClassNotFoundException ex)
				{
					throw new RuntimeException(ex);
				}
			}
		}
	}

	// Contributes the package "&lt;root&gt;.rest" (InternalConstants.TAPESTRY_APP_PACKAGE_PARAM + ".rest")
	// to the configuration, so that it will be scanned for annotated REST resource classes.
	@Contribute(ResteasyPackageManager.class)
	public static void resteasyPackageManager(Configuration<String> configuration,
	                                          @Symbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM) String appRootPackage,
	                                          @Symbol(ResteasySymbols.AUTOSCAN_REST_PACKAGE) Boolean shouldScanRestPackage)
	{
		if (shouldScanRestPackage)
		{
			configuration.add(appRootPackage + ".rest");
		}
	}

	public static ResteasyPackageManager buildResteasyPackageManager(final Collection<String> packageNames)
	{
		return new ResteasyPackageManager()
		{
			public Collection<String> getPackageNames()
			{
				return packageNames;
			}
		};
	}
}
