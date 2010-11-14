package org.tynamo.resteasy.integration;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

public class ResteasyIntegrationTest extends AbstractContainerTest
{
	@Test
	public void testPingResource() throws Exception
	{
		HtmlPage page = webClient.getPage(BASEURI + "mycustomresteasyprefix/ping");
		assertXPathPresent(page, "//h1[contains(text(),'PONG')]");
	}

	@Test
	public void testEchoResource() throws Exception
	{
		ClientRequest request = new ClientRequest(BASEURI + "mycustomresteasyprefix/echo/Hellow World!");
		ClientResponse<String> response = request.get(String.class);
		Assert.assertEquals(response.getStatus(), 200);
		Assert.assertEquals(response.getEntity(), "Hellow World!");
	}

}
