package de.mymiggi.movie.api;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.pub.GetMovieCountAction;

@Path("movie-archive/public")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PublicResource
{
	@GET
	@Path("movie-count")
	@PermitAll
	// Not working
	public Response countMovies()
	{
		return new GetMovieCountAction().run();
	}
}
