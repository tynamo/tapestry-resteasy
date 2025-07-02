package org.tynamo.resteasy.services;

import org.apache.tapestry5.commons.Configuration;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.http.internal.TapestryHttpInternalConstants;
import org.apache.tapestry5.http.internal.TapestryHttpInternalSymbols;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.tynamo.resteasy.ResteasyModule;
import org.tynamo.resteasy.ResteasyPackageManager;
import org.tynamo.resteasy.ResteasySymbols;
import org.tynamo.resteasy.rest.AutodiscoverableInjectableResource;
import org.tynamo.resteasy.ws.ReloadableEchoResource;
import org.tynamo.resteasy.ws.ReloadableEchoResourceImpl;

@ImportModule(ResteasyModule.class)
public class AppModule
{

	public static void bind(ServiceBinder binder)
	{
		binder.bind(ReloadableEchoResource.class, ReloadableEchoResourceImpl.class);
		binder.bind(AutodiscoverableInjectableResource.class);
	}


	/**
	 * Contributions to the RESTeasy main Application, insert all your RESTeasy singleton services here.
	 * 
	 * @param singletons
	 *          jax-rs singleton resources
	 * @param reloadableEchoResource
	 *          T5 service to be contributed as a REST resource
	 * 
	 */
	@Contribute(jakarta.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
	                                         ReloadableEchoResource reloadableEchoResource)
	{
		singletons.add(reloadableEchoResource);
	}

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void provideSymbols(MappedConfiguration<String, String> configuration)
	{
		configuration.add(ResteasySymbols.MAPPING_PREFIX, "/mycustomresteasyprefix");
		configuration.add(TapestryHttpInternalConstants.TAPESTRY_APP_PACKAGE_PARAM, "org.tynamo.resteasy");
		configuration.add(TapestryHttpInternalSymbols.APP_PACKAGE_PATH, "org/tynamo/resteasy");
	}

	@Contribute(ResteasyPackageManager.class)
	public static void resteasyPackageManager(Configuration<String> configuration)
	{
		configuration.add("org.tynamo.resteasy.ws.autobuild");
	}

}
