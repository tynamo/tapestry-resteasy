package org.tynamo.resteasy.ws;

import org.apache.tapestry5.json.JSONObject;

import jakarta.ws.rs.core.Response;

public class ReloadableEchoResourceImpl implements ReloadableEchoResource
{

	@Override
	public Response echo(String message)
	{
		return Response.status(200).entity(new JSONObject("message", message).toCompactString()).build();
	}

}