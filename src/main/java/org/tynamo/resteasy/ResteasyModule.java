package org.tynamo.resteasy;


import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ServiceBinder;

import javax.ws.rs.core.Application;

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
		binder.bind(Application.class, WsApplication.class);
	}

	public static void contributeIgnoredPathsFilter(Configuration<String> configuration)
	{
		configuration.add("/rest/.*");
	}

}
