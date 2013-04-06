package org.tynamo.resteasy;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.services.TapestryModule;
import org.apache.tapestry5.test.TapestryTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.resteasy.services.AppModule;
import org.tynamo.resteasy.services.DisableAutoScanModule;

public class DisableAutoScanTest extends TapestryTestCase
{

	@Test
	public void auto_scan_enabled()
	{
		RegistryBuilder builder = new RegistryBuilder();

		builder.add(TapestryModule.class);
		builder.add(ResteasyModule.class);
		builder.add(AppModule.class);

		Registry registry = builder.build();
		registry.performRegistryStartup();

		javax.ws.rs.core.Application app = registry.getService(javax.ws.rs.core.Application.class);
		Assert.assertEquals(app.getSingletons().size(), 5, "there are five services");

		registry.shutdown();

	}

	@Test
	public void auto_scan_disabled()
	{
		RegistryBuilder builder = new RegistryBuilder();

		builder.add(TapestryModule.class);
		builder.add(ResteasyModule.class);
		builder.add(AppModule.class);
		builder.add(DisableAutoScanModule.class);


		Registry registry = builder.build();
		registry.performRegistryStartup();

		javax.ws.rs.core.Application app = registry.getService(javax.ws.rs.core.Application.class);
		Assert.assertEquals(app.getSingletons().size(), 2, "there are two services");

		registry.shutdown();
	}

}