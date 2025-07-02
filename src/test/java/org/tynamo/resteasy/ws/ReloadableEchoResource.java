package org.tynamo.resteasy.ws;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/echo")
public interface ReloadableEchoResource
{
	@GET
	@Path("/{message}")
	@Produces("application/json")
	Response echo(@PathParam("message") String message);
}
