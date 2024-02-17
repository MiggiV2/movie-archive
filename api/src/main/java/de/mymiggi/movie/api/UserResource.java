package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.user.GetMovieByIDAction;
import de.mymiggi.movie.api.actions.user.GetMoviesAction;
import de.mymiggi.movie.api.actions.user.GetSortedMoviesAction;
import de.mymiggi.movie.api.actions.user.SearchAction;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.service.SyncService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

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
	public List<MovieEntity> getMovieListByPage(@QueryParam("page") int page)
	{
		return getMoviesAction.run(page, defaultPage);
	}

	@GET
	@Path("get-movie-by-id")
	public MovieEntity getMovieByID(@QueryParam("id") long id)
	{
		return getMovieByIDAction.run(id);
	}

	@GET
	@Path("sorted-movies/by-name")
	public List<MovieEntity> getNameSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return sortedMoviesAction.runByName(page, desc, defaultPage);
	}

	@GET
	@Path("sorted-movies/by-year")
	public List<MovieEntity> getYearSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return sortedMoviesAction.runByYear(page, desc, defaultPage);
	}

	@GET
	@Path("search")
	public List<MovieEntity> searchMovie(@QueryParam("query") String query)
	{
		return searchAction.run(query);
	}

	@GET
	@Path("sync")
	public Map<Long, String> getHashMap()
	{
		return syncService.getHashMap();
	}
}
