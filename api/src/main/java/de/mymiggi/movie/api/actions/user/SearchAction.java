package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
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

	private List<MovieEntity> search(String query)
	{
		List<MovieEntity> results = MovieEntity.find("name ILIKE ?1 ORDER BY name", query + "%").list();
		if (!results.isEmpty())
		{
			return results;
		}
		results = MovieEntity.find("name ILIKE ?1 ORDER BY name", "%" + query + "%").list();
		if (!results.isEmpty())
		{
			return results;
		}
		return new ArrayList<>();
	}
}