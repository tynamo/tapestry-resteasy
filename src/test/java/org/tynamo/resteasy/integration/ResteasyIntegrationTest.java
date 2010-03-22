package org.tynamo.resteasy.integration;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
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

}
