package de.mymiggi.movie.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.user.GetMoviesAction;
import io.quarkus.security.identity.SecurityIdentity;

@Path("movie-archive/user")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserResource
{
	@Inject
	SecurityIdentity identity;

	@GET
	@Path("get-movies")
	public Response getMovieListByPage(@QueryParam("page") int page)
	{
		return new GetMoviesAction().run(page);
	}
}
