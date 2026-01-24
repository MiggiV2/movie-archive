package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.DeleteMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.actions.user.AddTagsAction;
import de.mymiggi.movie.api.actions.user.GetMovieByIDAction;
import de.mymiggi.movie.api.actions.user.GetMoviesAction;
import de.mymiggi.movie.api.actions.user.GetSortedMoviesAction;
import de.mymiggi.movie.api.actions.user.SearchAction;
import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.TagEntity;
import de.mymiggi.movie.api.entity.db.TagMovieRelation;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("movie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource
{
	@Inject
	DefaultPage defaultPage;
	@Inject
	AddMovieAction addMovieAction;
	@Inject
	UpdateMovieAction updateMovieAction;
	@Inject
	DeleteMovieAction deleteMovieAction;
	@Inject
	GetSortedMoviesAction sortedMoviesAction;
	@Inject
	GetMoviesAction getMoviesAction;
	@Inject
	GetMovieByIDAction getMovieByIDAction;
	@Inject
	AddTagsAction addTagsAction;
	@Inject
	SearchAction searchAction;

	@POST
	@Transactional
	public DetailedMovie addMovie(DetailedMovie movieEntity)
	{
		return addMovieAction.run(movieEntity);
	}

	@PUT
	@Transactional
	public DetailedMovie updateMovie(DetailedMovie movieEntity)
	{
		return updateMovieAction.run(movieEntity);
	}

	@DELETE
	@Transactional
	public void deleteMovieByID(@QueryParam("id") Long id)
	{
		deleteMovieAction.run(id);
	}

	@GET
	public List<MovieEntity> getMovieListByPage(@QueryParam("page") int page)
	{
		return getMoviesAction.run(page, defaultPage);
	}

	@GET
	@Path("preview/by-name")
	// ToDo: Remove by-name / by-year endpoints later
	public List<MoviePreview> previewNameSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return sortedMoviesAction.previewsByName(page, desc, defaultPage);
	}

	@GET
	@Path("preview/by-year")
	public List<MoviePreview> previewYearSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return sortedMoviesAction.previewsByYear(page, desc, defaultPage);
	}

	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public long getMovieCount()
	{
		return MovieEntity.count();
	}

	@GET
	@PermitAll
	@Path("pages")
	@Produces(MediaType.TEXT_PLAIN)
	public long getMoviePageCount()
	{
		return Math.ceilDiv(MovieEntity.count(), defaultPage.Size()) - 1;
	}

	@GET
	@Path("search")
	public List<MoviePreview> searchMovie(@QueryParam("query") String query)
	{
		return searchAction.run(query);
	}

	@GET
	@Path("{movie-id}")
	public DetailedMovie getMovieByID(@PathParam("movie-id") Long id)
	{
		return getMovieByIDAction.run(id);
	}

	@POST
	@Path("{movie-id}/tags")
	@Transactional
	public void addTagsToMovie(@PathParam("movie-id") Long movieId, String[] tags)
	{
		addTagsAction.run(movieId, tags);
	}

	@GET
	@Path("{movie-id}/tags")
	public List<TagEntity> listTagsForMovie(@PathParam("movie-id") Long movieId)
	{
		MovieEntity movieEntity = MovieEntity.findById(movieId);
		if (movieEntity == null)
		{
			throw new NotFoundException("No movie found!");
		}
		List<TagMovieRelation> relations = TagMovieRelation.find("movie", movieEntity).list();
		return relations.stream().map(TagMovieRelation::getTag).toList();
	}
}
