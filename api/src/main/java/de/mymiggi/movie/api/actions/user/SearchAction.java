package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.SearchWrapper;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.util.List;

@ApplicationScoped
public class SearchAction
{
	public List<MovieEntity> run(String query)
	{
		if (query == null || query.isBlank())
		{
			throw new BadRequestException("You need a search query!");
		}
		return search(query);
	}

	/**
	 * <h3>After some extreme tests:</h3>
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
