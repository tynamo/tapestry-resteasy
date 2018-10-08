package org.tynamo.resteasy.ws;

import org.apache.tapestry5.json.JSONObject;

import javax.ws.rs.core.Response;
import java.util.List;

public class ReloadableEchoResourceImpl implements ReloadableEchoResource
{

	@Override
	public Response echo(String message)
	{
		return Response.status(200).entity(new JSONObject("message", message).toCompactString()).build();
	}

	@Override
	public Response genericLongs(List<Long> params)
	{
		Long first = params.get(0); // java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.Long
		return Response.ok(first).build();
	}

	@Override
	public Response genericLongsWorkaround(GenericLongsRequest request)
	{
		Long first = request.params.get(0); // OK
		return Response.ok(first).build();
	}

}