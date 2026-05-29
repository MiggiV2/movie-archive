package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.Optional;

@ApplicationScoped
public class GetMovieByIDAction
{
	public DetailedMovie run(long id)
	{
		Optional<MovieEntity> movieEntity = MovieEntity.findByIdOptional(id);
		if (movieEntity.isEmpty())
		{
			throw new NotFoundException("Movie not found!");
		}
		MovieEntity movie = movieEntity.get();
		Optional<MovieMetaData> metaData = MovieMetaData.find("movieEntity", movie).firstResultOptional();
		return new DetailedMovie(movie, metaData.orElse(new MovieMetaData()));
	}
}
