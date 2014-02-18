package org.tynamo.resteasy.ws;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Api(value = "/echo", description = "the ECHO api")
@Path("/echo")
public interface ReloadableEchoResource
{
	@GET
	@Path("/{message}")
	@Produces("application/json")
	@ApiOperation("echoes a message")
	Response echo(@PathParam("message") String message);
}
