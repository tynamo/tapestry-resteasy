package org.tynamo.resteasy.ws.autobuild;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/ping")
public class PingResource
{

	@GET
	@Produces("text/html")
	public String ping()
	{
		return "<html><h1>PONG</h1></html>";
	}

}
