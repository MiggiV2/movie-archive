package de.mymiggi.movie.api.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.actions.user.GetMovieByIDAction;
import de.mymiggi.movie.api.actions.user.GetMoviesAction;
import de.mymiggi.movie.api.actions.user.SearchAction;
import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

/**
 * MCP tools exposing the movie archive to AI assistants. Each tool delegates to an existing
 * action/service so behavior matches the REST API (search semantics, uuid generation, IMDb
 * metadata enrichment, audit logging). Authenticated via the API-token Bearer mechanism; read
 * tools need any valid token, write tools require an ADMIN-role token.
 * <p>
 * Genre discovery operates on {@code MovieMetaData.genres} (the IMDb genres shown in the UI),
 * not the legacy {@code TagEntity} system, which the current UI no longer uses.
 */
@Authenticated
@ApplicationScoped
public class MovieTools
{
	@Inject
	SearchAction searchAction;
	@Inject
	GetMovieByIDAction getMovieByIDAction;
	@Inject
	AddMovieAction addMovieAction;
	@Inject
	UpdateMovieAction updateMovieAction;
	@Inject
	EntityManager entityManager;
	@Inject
	ObjectMapper objectMapper;

	@Tool(name = "search_movies", description = "Search the movie archive by free text across the title. "
		+ "Returns matching movies with id, title and year. Use it for recommendations (e.g. 'thriller') "
		+ "or to count how many movies match a term (e.g. 'Star Wars'). Returns an empty list when nothing matches.")
	public List<MoviePreview> searchMovies(
		@ToolArg(description = "Free-text search query, e.g. 'thriller' or 'Star Wars'") String query)
	{
		return searchAction.run(query);
	}

	@Tool(name = "list_genres", description = "List all distinct genres in the archive (the IMDb genres shown in the UI, "
		+ "e.g. Thriller, Drama). Use these for genre/mood recommendations.")
	@SuppressWarnings("unchecked")
	public List<String> listGenres()
	{
		return entityManager
			.createNativeQuery("SELECT DISTINCT g FROM moviemetadata, unnest(genres) AS g WHERE genres IS NOT NULL ORDER BY g", String.class)
			.getResultList();
	}

	@Tool(name = "find_movies_by_genre", description = "Find movies of a given genre (case-insensitive), e.g. 'thriller'. "
		+ "Returns movies with id, title and year. Use list_genres to discover available genres.")
	@SuppressWarnings("unchecked")
	public List<MoviePreview> findMoviesByGenre(
		@ToolArg(description = "A genre name, e.g. 'Thriller' (case-insensitive)") String genre)
	{
		List<MovieEntity> movies = entityManager
			.createNativeQuery("SELECT m.* FROM movieentity m JOIN moviemetadata md ON md.movieentity_id = m.id "
				+ "WHERE EXISTS (SELECT 1 FROM unnest(md.genres) gg WHERE lower(gg) = lower(?1)) ORDER BY m.name", MovieEntity.class)
			.setParameter(1, genre)
			.getResultList();
		return movies.stream().map(GetMoviesAction::enrichMovie).toList();
	}

	@Tool(name = "get_movie", description = "Get the full detail (metadata, tags, etc.) for a single movie by id.")
	public ToolResponse getMovie(
		@ToolArg(description = "The movie id") long id)
	{
		try
		{
			return ToolResponse.success(toJson(getMovieByIDAction.run(id)));
		}
		catch (NotFoundException e)
		{
			return ToolResponse.success("No movie found with id " + id);
		}
	}

	@Tool(name = "create_movie", description = "Add a new movie to the archive (requires an ADMIN token). "
		+ "Metadata is auto-enriched from IMDb; leave externalId empty to auto-detect.")
	@RolesAllowed("${de.mymiggi.admin-role}")
	@Transactional
	public DetailedMovie createMovie(
		@ToolArg(description = "Movie title") String title,
		@ToolArg(description = "Media type, e.g. BD, DVD, 4K") String type,
		@ToolArg(description = "Release year") int year,
		@ToolArg(description = "Shelf/block location", required = false) String block,
		@ToolArg(description = "Wikipedia URL", required = false) String wikiUrl,
		@ToolArg(description = "Original (untranslated) title", required = false) String originalName,
		@ToolArg(description = "IMDb id; leave empty to auto-detect", required = false) String externalId)
	{
		return addMovieAction.run(build(0, title, type, year, block, wikiUrl, originalName, externalId));
	}

	@Tool(name = "update_movie", description = "Update an existing movie by id (requires an ADMIN token). "
		+ "The change is audit-logged.")
	@RolesAllowed("${de.mymiggi.admin-role}")
	@Transactional
	public DetailedMovie updateMovie(
		@ToolArg(description = "The id of the movie to update") long id,
		@ToolArg(description = "Movie title") String title,
		@ToolArg(description = "Media type, e.g. BD, DVD, 4K") String type,
		@ToolArg(description = "Release year") int year,
		@ToolArg(description = "Shelf/block location", required = false) String block,
		@ToolArg(description = "Wikipedia URL", required = false) String wikiUrl,
		@ToolArg(description = "Original (untranslated) title", required = false) String originalName,
		@ToolArg(description = "IMDb id", required = false) String externalId)
	{
		return updateMovieAction.run(build(id, title, type, year, block, wikiUrl, originalName, externalId));
	}

	private DetailedMovie build(long id, String title, String type, int year, String block,
		String wikiUrl, String originalName, String externalId)
	{
		DetailedMovie movie = new DetailedMovie();
		movie.setId(id);
		movie.setTitle(title);
		movie.setType(type);
		movie.setYear(year);
		movie.setBlock(block);
		movie.setWikiUrl(wikiUrl);
		movie.setOriginalName(originalName);
		movie.setExternalId(externalId);
		return movie;
	}

	private String toJson(DetailedMovie movie)
	{
		try
		{
			return objectMapper.writeValueAsString(movie);
		}
		catch (JsonProcessingException e)
		{
			throw new IllegalStateException("Failed to serialize movie", e);
		}
	}
}
