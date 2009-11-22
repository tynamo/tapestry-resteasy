package org.tynamo.resteasy.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.tynamo.resteasy.ResteasyModule;
import org.tynamo.resteasy.ws.PingResource;

@SubModule(ResteasyModule.class)
public class AppModule
{

	/**
	 * Contributions to the RESTeasy main Application, insert all your
	 * RESTeasy singleton services here.
	 */
	public static void contributeApplication(Configuration<Object> singletons, ObjectLocator locator)
	{
		singletons.add(locator.autobuild(PingResource.class));
	}

}
