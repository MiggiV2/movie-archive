package de.mymiggi.movie.api.actions.admin;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class AddMovieAction
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
		String firstLetter = String.valueOf(movieEntity.name.charAt(0));
		movieEntity.uuid = firstLetter + MovieEntity.startWith(firstLetter).size();
		movieEntity.persist();
		return Response.ok(movieEntity).build();
	}

	private boolean checkMovie(MovieEntity movieEntity)
	{
		return movieEntity.name != null && movieEntity.type != null && movieEntity.year != 0;
	}
}
