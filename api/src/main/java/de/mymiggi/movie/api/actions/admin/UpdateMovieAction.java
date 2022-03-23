package de.mymiggi.movie.api.actions.admin;

import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class UpdateMovieAction
{
	public Response run(MovieEntity movieEntity)
	{
		if (movieEntity == null)
		{
			ShortMessage message = new ShortMessage("You need an object!", MessageStatus.ERROR);
			return Response.status(Status.BAD_REQUEST).entity(message).build();
		}
		if (!checkMovie(movieEntity))
		{
			ShortMessage message = new ShortMessage("You object needs: String name, String type, int year!", MessageStatus.ERROR);
			return Response.status(Status.BAD_REQUEST).entity(message).build();
		}
		Optional<MovieEntity> dbEntity = MovieEntity.findByIdOptional(movieEntity.id);
		if (dbEntity.isEmpty())
		{
			ShortMessage message = new ShortMessage("I can't find your movie by id " + movieEntity.id, MessageStatus.ERROR);
			return Response.status(Status.NOT_FOUND).entity(message).build();
		}
		dbEntity.ifPresent(entity -> {
			update(entity, movieEntity);
			entity.persist();
		});
		return Response.ok(movieEntity).build();
	}

	private void update(MovieEntity entityOld, MovieEntity entityNew)
	{
		entityOld.block = entityNew.block;
		entityOld.name = entityNew.name;
		entityOld.type = entityNew.type;
		entityOld.uuid = entityNew.uuid;
		entityOld.wikiUrl = entityNew.wikiUrl;
		entityOld.year = entityNew.year;
	}

	private boolean checkMovie(MovieEntity movieEntity)
	{
		return movieEntity.name != null && movieEntity.type != null && movieEntity.year != 0 && movieEntity.id != null;
	}
}
