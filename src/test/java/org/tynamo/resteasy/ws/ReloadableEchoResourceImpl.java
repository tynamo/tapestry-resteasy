package org.tynamo.resteasy.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/echo")
public class ReloadableEchoResourceImpl implements ReloadableEchoResource
{

	@GET
	@Path("/{message}")
	@Override
public Response echo(@PathParam("message") String message)
{
	return Response.status(200).entity(message).build();
}

}