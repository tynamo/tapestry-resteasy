package org.tynamo.resteasy;


import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ClassNameLocator;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.jboss.resteasy.util.GetRestful;

import java.util.Collection;

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
		binder.bind(javax.ws.rs.core.Application.class, org.tynamo.resteasy.Application.class);
		binder.bind(HttpServletRequestFilter.class, ResteasyRequestFilter.class).withId("ResteasyRequestFilter");
	}

	public static void contributeHttpServletRequestHandler(OrderedConfiguration<HttpServletRequestFilter> configuration,
	                                                       @InjectService("ResteasyRequestFilter")
	                                                       HttpServletRequestFilter resteasyRequestFilter)
	{
		configuration.add("ResteasyRequestFilter", resteasyRequestFilter);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration)
	{
		configuration.add(ResteasySymbols.MAPPING_PREFIX, "/rest");
	}

	public static void contributeApplication(Configuration<Object> singletons,
	                                         ObjectLocator locator,
	                                         ResteasyPackageManager resteasyPackageManager,
	                                         ClassNameLocator classNameLocator)
	{
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		for (String packageName : resteasyPackageManager.getPackageNames())
		{
			for (String className : classNameLocator.locateClassNames(packageName))
			{
				try
				{
					Class entityClass = contextClassLoader.loadClass(className);

					if (GetRestful.isRootResource(entityClass))
					{
						singletons.add(locator.autobuild(entityClass));
					}
				}

				catch (ClassNotFoundException ex)
				{
					throw new RuntimeException(ex);
				}
			}
		}
	}

	/**
	 * Contributes the package "&lt;root&gt;.rest" (InternalConstants.TAPESTRY_APP_PACKAGE_PARAM + ".rest")
	 * to the configuration, so that it will be scanned for annotated REST resource classes.
	 */
	public static void contributeResteasyPackageManager(Configuration<String> configuration,
	                                                    @Inject
	                                                    @Symbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM)
	                                                    String appRootPackage)
	{
		configuration.add(appRootPackage + ".rest");
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
