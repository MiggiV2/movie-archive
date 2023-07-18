package de.mymiggi.movie.api.actions.user;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.SearchWrapper;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

@ApplicationScoped
public class SearchAction
{
	public Response run(String query)
	{
		if (query == null || query.isBlank())
		{
			ShortMessage message = new ShortMessage("You need a search query!", MessageStatus.ERROR);
			return Response.status(Status.BAD_REQUEST).entity(message).build();
		}
		List<MovieEntity> result = search(query);
		return result.isEmpty()
			? Response.noContent().build()
			: Response.ok(result).build();
	}

	/**
	 * <h3>After some extrem tests:</h3>
	 * <p>
	 * This stream method is ~5.85% - <0.9% slower [JVM dev mode]
	 * </p>
	 * <p>
	 * BUT it's faster via <b>GralVM</b>! Up to 5.5% <b>FASTER</b>
	 * </p>
	 *
	 * @return sorted result
	 */
	private List<MovieEntity> search(String query)
	{
		List<MovieEntity> allMovies = MovieEntity.listAll();
		return allMovies.stream()
			.map(m -> new SearchWrapper(m, query))
			.sorted()
			.filter(m -> m.getMatchesQuery() > 0)
			.map(SearchWrapper::getMovie)
			.toList();
	}
}
