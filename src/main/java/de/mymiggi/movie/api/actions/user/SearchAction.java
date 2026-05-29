package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class SearchAction
{
	public List<MoviePreview> run(String query)
	{
		if (query == null || query.isBlank())
		{
			throw new BadRequestException("You need a search query!");
		}
		return search(query).stream()
			.map(GetMoviesAction::enrichMovie)
			.toList();
	}

	private List<MovieEntity> search(String query)
	{
		String sqlQuery = "name ILIKE ?1 ORDER BY name";
		List<MovieEntity> results = MovieEntity.find(sqlQuery, query + "%").list();
		if (!results.isEmpty())
		{
			return results;
		}
		results = MovieEntity.find(sqlQuery, "%" + query + "%").list();
		if (!results.isEmpty())
		{
			return results;
		}
		return findMoviesContainingWords(query);
	}

	private List<MovieEntity> findMoviesContainingWords(String query)
	{
		String[] words = query.split("\\s+");
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < words.length; i++)
		{
			if (i > 0)
			{
				sb.append(" AND ");
			}

			sb.append("name ILIKE ?").append(i + 1);
		}

		String sqlQuery = sb.toString();
		Object[] args = Arrays.stream(words)
			.map(w -> "%" + w.toLowerCase() + "%")
			.toArray();
		return MovieEntity.find(sqlQuery, args).list();
	}
}