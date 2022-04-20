package de.mymiggi.movie.api.actions.admin;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
import de.mymiggi.movie.api.actions.auditlog.SaveAuditLogAction;
import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.KeycloakUser;
import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class AddMovieAction extends AbstractAuditLogAction
{
	public Response run(MovieEntity movieEntity, KeycloakUser user)
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
		new SaveAuditLogAction().run(user, this, String.format("Added movie '%s'", movieEntity.name), movieEntity);
		return Response.ok(movieEntity).build();
	}

	private boolean checkMovie(MovieEntity movieEntity)
	{
		return movieEntity.name != null && movieEntity.type != null && movieEntity.year != 0;
	}

	@Override
	public AuditLogType getLogType()
	{
		return AuditLogType.ADD;
	}
}
