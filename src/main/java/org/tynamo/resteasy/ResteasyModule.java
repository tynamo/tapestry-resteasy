package org.tynamo.resteasy;


import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.HttpServletRequestFilter;

/**
 * This module is automatically included as part of the Tapestry IoC Registry
 */
public class ResteasyModule {

	public static void bind(ServiceBinder binder) {
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		binder.bind(javax.ws.rs.core.Application.class, org.tynamo.resteasy.Application.class);
		binder.bind(HttpServletRequestFilter.class, ResteasyRequestFilter.class).withId("ResteasyRequestFilter");
	}

	public static void contributeHttpServletRequestHandler(OrderedConfiguration<HttpServletRequestFilter> configuration,
	                                                       @InjectService("ResteasyRequestFilter")
	                                                       HttpServletRequestFilter resteasyRequestFilter) {
		configuration.add("ResteasyRequestFilter", resteasyRequestFilter);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(ResteasySymbols.MAPPING_PREFIX, "/rest");
	}

}
