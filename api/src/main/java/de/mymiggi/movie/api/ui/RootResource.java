package de.mymiggi.movie.api.ui;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
public class RootResource
{
	@GET
	public Response redirectToUi()
	{
		return Response.seeOther(URI.create("/ui")).build();
	}
}
