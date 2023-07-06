package de.mymiggi.movie.api.actions.helper;

import java.util.List;
import java.util.stream.Collectors;

import de.mymiggi.movie.api.entity.SearchWrapper;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class SimpleSearchAction
{
	public List<MovieEntity> run(String query)
	{
		List<MovieEntity> allMovies = MovieEntity.listAll();
		return allMovies.stream()
			.map(m -> new SearchWrapper(m, query))
			.sorted()
			.filter(m -> m.getMatchesQuery() > 0)
			.map(SearchWrapper::getMovie)
			.collect(Collectors.toList());
	}
}
