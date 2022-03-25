package de.mymiggi.movie.api.actions.admin;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class DeleteMovieAction 
{
	public Response run(MovieEntity movieEntity)
	{
		ShortMessage message = new ShortMessage("Can't find your movie!", MessageStatus.ERROR);
		if(!movieEntity.isPersistent())
		{
			return Response.status(Status.NOT_FOUND).entity(message).build();
		}
		movieEntity.delete();
		return Response.ok().build();
	}
}
