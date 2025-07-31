package de.mymiggi.movie.api;

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
import de.mymiggi.movie.api.service.ExportService;
import de.mymiggi.movie.api.service.SyncService;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("movie-archive/user")
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
	ExportService exportService;

	@Inject
	public UserResource(DefaultPage defaultPage, SyncService syncService, GetSortedMoviesAction sortedMoviesAction,
		GetMoviesAction getMoviesAction, GetMovieByIDAction getMovieByIDAction, SearchAction searchAction, ExportService exportService)
	{
		this.defaultPage = defaultPage;
		this.syncService = syncService;
		this.sortedMoviesAction = sortedMoviesAction;
		this.getMoviesAction = getMoviesAction;
		this.getMovieByIDAction = getMovieByIDAction;
		this.searchAction = searchAction;
		this.exportService = exportService;
	}

	@GET
	@Path("get-movies")
	public List<MovieEntity> getMovieListByPage(@QueryParam("page") int page)
	{
		return getMoviesAction.run(page, defaultPage);
	}

	@GET
	@Path("get-movie-by-id")
	public DetailedMovie getMovieByID(@QueryParam("id") long id)
	{
		return getMovieByIDAction.run(id);
	}

	@GET
	@Path("preview-movies/by-name")
	public List<MoviePreview> previewNameSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return sortedMoviesAction.previewsByName(page, desc, defaultPage);
	}

	@GET
	@Path("preview-movies/by-year")
	public List<MoviePreview> previewYearSortedMovies(@QueryParam("page") int page, @QueryParam("desc") boolean desc)
	{
		return sortedMoviesAction.previewsByYear(page, desc, defaultPage);
	}

	@GET
	@Path("search")
	public List<MoviePreview> searchMovie(@QueryParam("query") String query)
	{
		return searchAction.run(query);
	}

	@GET
	@Path("sync")
	public Map<Long, String> getHashMap()
	{
		return syncService.getHashMap();
	}

	@GET
	@Path("tags")
	public List<TagEntity> listTags()
	{
		return TagEntity.listAll(Sort.by("name"));
	}

	@GET
	@Path("tags/{id}")
	public List<MovieEntity> listTaggedMovies(@PathParam("id") Long tagId)
	{
		TagEntity tag = TagEntity.findById(tagId);
		if (tag == null)
		{
			throw new NotFoundException("Tag not found!");
		}
		List<TagMovieRelation> relations = TagMovieRelation.find("tag", tag).list();
		return relations.stream().map(TagMovieRelation::getMovie).toList();
	}

	@GET
	@Path("tags/by-movie/{id}")
	public List<TagEntity> listTagsForMovie(@PathParam("id") Long movieId)
	{
		MovieEntity movieEntity = MovieEntity.findById(movieId);
		if (movieEntity == null)
		{
			throw new NotFoundException("No movie found!");
		}
		List<TagMovieRelation> relations = TagMovieRelation.find("movie", movieEntity).list();
		return relations.stream().map(TagMovieRelation::getTag).toList();
	}

	@GET
	@Path("export/session")
	@Produces(MediaType.TEXT_PLAIN)
	public String getExportOneTimeSession()
	{
		return exportService.createOneTimeSession();
	}
}

