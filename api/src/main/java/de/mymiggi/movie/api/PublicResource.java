package de.mymiggi.movie.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.pub.GetMovieCountAction;
import de.mymiggi.movie.api.actions.pub.GetMoviePageCount;
import de.mymiggi.movie.api.entity.config.DefaultPage;

@Path("movie-archive/public")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PublicResource
{
	@Inject
	DefaultPage defaultPage;

	@GET
	@Path("movie-count")
	public Response getMovieCount()
	{
		return new GetMovieCountAction().run();
	}

	@GET
	@Path("movie-page-count")
	public Response getMoviePageCount()
	{
		return new GetMoviePageCount().run(defaultPage);
	}
}
