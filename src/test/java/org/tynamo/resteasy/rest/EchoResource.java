package org.tynamo.resteasy.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/echo")
public class EchoResource
{

	@POST
	public String echo(String body)
	{
		return body;
	}

}
