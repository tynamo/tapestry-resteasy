package org.tynamo.resteasy.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.tynamo.resteasy.ResteasyModule;
import org.tynamo.resteasy.ResteasySymbols;

@SubModule(ResteasyModule.class)
public class AppModule
{

	/**
	 * Contributions to the RESTeasy main Application, insert all your RESTeasy singleton services here.
	 * <p/>
	 * @note: this is here for documentation purposes we don't need this anymore, the PingResource will be added by
	 * ResteasyPackageManager
	 */
/*
	public static void contributeApplication(Configuration<Object> singletons, ObjectLocator locator)
	{
		singletons.add(locator.autobuild(PingResource.class));
	}
*/

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
	{
		configuration.add(ResteasySymbols.MAPPING_PREFIX, "/mycustomresteasyprefix");
	}

	public static void contributeResteasyPackageManager(Configuration<String> configuration)
	{
		configuration.add("org.tynamo.resteasy.ws");
	}

}
