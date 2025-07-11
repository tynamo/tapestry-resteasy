package org.tynamo.resteasy.integration;

import org.htmlunit.html.HtmlPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;

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

}
