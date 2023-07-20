package de.mymiggi.movie.api.actions.admin;

import java.util.Optional;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
import de.mymiggi.movie.api.actions.auditlog.SaveAuditLogAction;
import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.KeycloakUser;
import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class DeleteMovieAction extends AbstractAuditLogAction
{
	public Response run(Long id, KeycloakUser user)
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
		movieEntity.ifPresent(movie -> {
			movie.delete();
			new SaveAuditLogAction().run(user, this, String.format("Removed movie '%s'", movie.name), movie);
		});
		return Response.noContent().build();
	}

	@Override
	public AuditLogType getLogType()
	{
		return AuditLogType.DELETE;
	}
}
