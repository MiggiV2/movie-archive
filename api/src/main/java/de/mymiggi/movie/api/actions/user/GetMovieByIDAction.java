package de.mymiggi.movie.api.actions.user;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.db.MovieEntity;

@ApplicationScoped
public class GetMovieByIDAction
{
	public Response run(long id)
	{
		Optional<MovieEntity> movieEntity = MovieEntity.findByIdOptional(id);
		return movieEntity.isPresent()
			? Response.ok(movieEntity.get()).build()
			: Response.status(Status.NOT_FOUND).build();
	}
}
