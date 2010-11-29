package org.tynamo.resteasy.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.tynamo.resteasy.ws.ReloadableEchoResource;
import org.tynamo.resteasy.ws.ReloadableEchoResourceImpl;
import org.tynamo.resteasy.ResteasyModule;
import org.tynamo.resteasy.ResteasySymbols;

@SubModule(ResteasyModule.class)
public class AppModule
{

	public static void bind(ServiceBinder binder)
	{
		binder.bind(ReloadableEchoResource.class, ReloadableEchoResourceImpl.class);
	}


	/**
	 * Contributions to the RESTeasy main Application, insert all your RESTeasy singleton services here.
	 * <p/>
	 *
	 */
	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
	                                         ReloadableEchoResource reloadableEchoResource)
	{
		singletons.add(reloadableEchoResource);
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
	{
		configuration.add(ResteasySymbols.MAPPING_PREFIX, "/mycustomresteasyprefix");
	}

	public static void contributeResteasyPackageManager(Configuration<String> configuration)
	{
		configuration.add("org.tynamo.resteasy.ws.autobuild");
	}

}
