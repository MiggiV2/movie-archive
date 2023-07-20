package de.mymiggi.movie.api;

import jakarta.annotation.security.RolesAllowed;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.user.GetMovieByIDAction;
import de.mymiggi.movie.api.actions.user.GetMoviesAction;
import de.mymiggi.movie.api.actions.user.GetSortedMoviesAction;
import de.mymiggi.movie.api.actions.user.SearchAction;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.service.SyncService;

@Path("movie-archive/user")
@RolesAllowed({ "user", "admin" })
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource
{
	DefaultPage defaultPage;
	SyncService syncService;
	GetSortedMoviesAction sortedMoviesAction;
	GetMoviesAction getMoviesAction;
	GetMovieByIDAction getMovieByIDAction;
	SearchAction searchAction;

	@Inject
	public UserResource(DefaultPage defaultPage, SyncService syncService, GetSortedMoviesAction sortedMoviesAction,
		GetMoviesAction getMoviesAction, GetMovieByIDAction getMovieByIDAction, SearchAction searchAction)
	{
		this.defaultPage = defaultPage;
		this.syncService = syncService;
		this.sortedMoviesAction = sortedMoviesAction;
		this.getMoviesAction = getMoviesAction;
		this.getMovieByIDAction = getMovieByIDAction;
		this.searchAction = searchAction;
	}

	@GET
	@Path("get-movies")
	public Response getMovieListByPage(@QueryParam("page") int page)
	{
		return getMoviesAction.run(page, defaultPage);
	}

	@GET
	@Path("get-movie-by-id")
	public Response getMovieByID(@QueryParam("id") long id)
	{
		return getMovieByIDAction.run(id);
	}

	@GET
	@Path("sorted-movies/by-name")
	public Response getNameSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return sortedMoviesAction.runByName(page, desc, defaultPage);
	}

	@GET
	@Path("sorted-movies/by-year")
	public Response getYearSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return sortedMoviesAction.runByYear(page, desc, defaultPage);
	}

	@GET
	@Path("search")
	public Response searchMovie(@QueryParam("query") String query)
	{
		return searchAction.run(query);
	}

	@GET
	@Path("sync")
	public Response getHashMap()
	{
		return Response.ok(syncService.getHashMap()).build();
	}
}
