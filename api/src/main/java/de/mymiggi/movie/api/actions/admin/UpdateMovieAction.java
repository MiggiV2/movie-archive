package de.mymiggi.movie.api.actions.admin;

import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
import de.mymiggi.movie.api.actions.auditlog.SaveAuditLogAction;
import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.KeycloakUser;
import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class UpdateMovieAction extends AbstractAuditLogAction
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
		Optional<MovieEntity> dbEntity = MovieEntity.findByIdOptional(movieEntity.id);
		if (dbEntity.isEmpty())
		{
			ShortMessage message = new ShortMessage("I can't find your movie by id " + movieEntity.id, MessageStatus.ERROR);
			return Response.status(Status.NOT_FOUND).entity(message).build();
		}
		dbEntity.ifPresent(entity -> {
			String message = getUpdateMessage(entity, movieEntity);
			update(entity, movieEntity);
			entity.persist();
			new SaveAuditLogAction().run(user, this, message, movieEntity);
		});
		return Response.ok(movieEntity).build();
	}

	private String getUpdateMessage(MovieEntity entityOld, MovieEntity entityNew)
	{
		if (!entityOld.name.equals(entityNew.name))
		{
			return String.format("Updated movie name from '%s' to '%s'", entityOld.name, entityNew.name);
		}
		if (!entityOld.wikiUrl.equals(entityNew.wikiUrl))
		{
			return String.format("Updated movie wiki-url from '%s' to '%s'", entityOld.wikiUrl, entityNew.wikiUrl);
		}
		if (!entityOld.block.equals(entityNew.block))
		{
			return String.format("Updated movie block from '%s' to '%s'", entityOld.block, entityNew.block);
		}
		if (entityOld.year != entityNew.year)
		{
			return String.format("Updated movie year from '%s' to '%s'", entityOld.year, entityNew.year);
		}
		return String.format("Updated movie '%s'", entityNew.name);
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

	@Override
	public AuditLogType getLogType()
	{
		return AuditLogType.UPDATE;
	}
}
