package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.Optional;

@ApplicationScoped
public class GetMovieByIDAction
{
	public MovieEntity run(long id)
	{
		Optional<MovieEntity> movieEntity = MovieEntity.findByIdOptional(id);
		if (movieEntity.isEmpty())
		{
			throw new NotFoundException("Movie not found!");
		}
		return movieEntity.get();
	}
}
