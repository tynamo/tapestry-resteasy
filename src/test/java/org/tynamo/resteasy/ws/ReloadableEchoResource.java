package org.tynamo.resteasy.ws;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public interface ReloadableEchoResource
{
	Response echo(@PathParam("message") String message);
}
