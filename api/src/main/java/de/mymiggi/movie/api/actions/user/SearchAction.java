package de.mymiggi.movie.api.actions.user;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.actions.helper.SimpleSearchAction;
import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class SearchAction
{
	public Response run(String query)
	{
		if (query == null || query.isBlank())
		{
			ShortMessage message = new ShortMessage("You need a search query!", MessageStatus.ERROR);
			return Response.status(Status.BAD_REQUEST).entity(message).build();
		}
		List<MovieEntity> result = new SimpleSearchAction().run(query);
		return result.isEmpty()
			? Response.noContent().build()
			: Response.ok(result).build();
	}
}
