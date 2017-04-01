package org.tynamo.resteasy.integration;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;

public class ResteasyIntegrationTest extends AbstractContainerTest
{

	@BeforeClass
	public void startContainer() throws Exception
	{
		String reserveNetworkPort = System.getProperty("reserved.network.port");

		if (reserveNetworkPort != null)
		{
			port = Integer.valueOf(reserveNetworkPort);
			BASEURI = "http://localhost:" + port + "/";
		}

		super.startContainer();
	}

	@Test
	public void testPingResource() throws Exception
	{
		HtmlPage page = webClient.getPage(BASEURI + "mycustomresteasyprefix/ping");
		assertXPathPresent(page, "//h1[contains(text(),'PONG')]");
	}

	@Test
	public void testEchoResource() throws Exception
	{
		Client client = ClientBuilder.newClient();
		Invocation.Builder builder = client.target(BASEURI + "mycustomresteasyprefix/echo/Hellow World!").request();
		String response = builder.get(String.class);
		Assert.assertEquals(response, "{\"message\":\"Hellow World!\"}");
		client.close();
	}

	@Test
	public void testEchoGenericListOfLongs() throws Exception
	{
		Client client = ClientBuilder.newClient();
		Invocation.Builder builder = client.target(BASEURI + "mycustomresteasyprefix/echo/generic_longs").request();
		String response = builder.post(Entity.json("[1, 2, 3]"), String.class);
		Assert.assertEquals(response, "1");
		client.close();
	}

	@Test
	public void testEchoGenericListOfLongsWorkaround() throws Exception
	{
		Client client = ClientBuilder.newClient();
		Invocation.Builder builder = client.target(BASEURI + "mycustomresteasyprefix/echo/generic_longs_workaround").request();
		String response = builder.post(Entity.json("{\"params\": [1, 2, 3]}"), String.class);
		Assert.assertEquals(response, "1");
		client.close();
	}
}
