package de.mymiggi.movie.api.actions.admin;

import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class DeleteMovieAction
{
	public Response run(Long id)
	{
		if (id == null)
		{
			ShortMessage message = new ShortMessage("We need an id for your movie!", MessageStatus.ERROR);
			return Response.status(Status.NOT_FOUND).entity(message).build();
		}
		Optional<MovieEntity> movieEntity = MovieEntity.findByIdOptional(id);
		if (movieEntity.isEmpty())
		{
			ShortMessage message = new ShortMessage("Can't find your movie!", MessageStatus.ERROR);
			return Response.status(Status.NOT_FOUND).entity(message).build();
		}
		movieEntity.ifPresent(MovieEntity::delete);
		return Response.ok().build();
	}
}
