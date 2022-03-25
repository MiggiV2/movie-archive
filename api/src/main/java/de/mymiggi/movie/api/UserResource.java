package de.mymiggi.movie.api;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.user.GetMovieByIDAction;
import de.mymiggi.movie.api.actions.user.GetMoviesAction;
import de.mymiggi.movie.api.actions.user.GetSortedMoviesAction;
import de.mymiggi.movie.api.actions.user.SearchAction;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import io.quarkus.security.identity.SecurityIdentity;

@Path("movie-archive/user")
@RolesAllowed({ "user", "admin" })
@ApplicationScoped
public class UserResource
{
	@Inject
	SecurityIdentity identity;
	@Inject
	DefaultPage defaultPage;

	@GET
	@Path("get-movies")
	public Response getMovieListByPage(@QueryParam("page") int page)
	{
		return new GetMoviesAction().run(page, defaultPage);
	}

	@GET
	@Path("get-movie-by-id")
	public Response getMovieByID(@QueryParam("id") long id)
	{
		return new GetMovieByIDAction().run(id);
	}

	@GET
	@Path("sorted-movies/by-name")
	public Response getNameSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return new GetSortedMoviesAction().runByName(page, desc, defaultPage);
	}

	@GET
	@Path("sorted-movies/by-year")
	public Response getYearSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return new GetSortedMoviesAction().runByYear(page, desc, defaultPage);
	}

	@GET
	@Path("search")
	public Response searchMovie(@QueryParam("query") String query)
	{
		return new SearchAction().run(query);
	}
}
