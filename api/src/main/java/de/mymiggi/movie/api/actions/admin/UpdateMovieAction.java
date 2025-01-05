package de.mymiggi.movie.api.actions.admin;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
import de.mymiggi.movie.api.actions.auditlog.SaveAuditLogAction;
import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.oidc.UserInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class UpdateMovieAction extends AbstractAuditLogAction
{
	@Inject
	UserInfo userInfo;

	public MovieEntity run(MovieEntity movieEntity)
	{
		if (movieEntity == null)
		{
			throw new BadRequestException("You need an object!");
		}
		if (!checkMovie(movieEntity))
		{
			throw new BadRequestException("You object needs: String name, String type, int year!");
		}
		Optional<MovieEntity> dbEntity = MovieEntity.findByIdOptional(movieEntity.id);
		if (dbEntity.isEmpty())
		{
			throw new NotFoundException("I can't find your movie by id " + movieEntity.id);
		}
		dbEntity.ifPresent(entity -> {
			String message = getUpdateMessage(entity, movieEntity);
			update(entity, movieEntity);
			entity.persist();
			new SaveAuditLogAction().run(userInfo, this, message, movieEntity);
		});
		return movieEntity;
	}

	private String getUpdateMessage(MovieEntity entityOld, MovieEntity entityNew)
	{
		if (!Objects.equals(entityOld.name, entityNew.name))
		{
			return String.format("Updated movie name from '%s' to '%s'", entityOld.name, entityNew.name);
		}
		if (!Objects.equals(entityOld.wikiUrl, entityNew.wikiUrl))
		{
			return String.format("Updated movie wiki-url from '%s' to '%s'", entityOld.wikiUrl, entityNew.wikiUrl);
		}
		if (!Objects.equals(entityOld.block, entityNew.block))
		{
			return String.format("Updated movie block from '%s' to '%s'", entityOld.block, entityNew.block);
		}
		if (entityOld.year != entityNew.year)
		{
			return String.format("Updated movie year from '%s' to '%s'", entityOld.year, entityNew.year);
		}
		if (!Objects.equals(entityOld.originalName, entityNew.originalName))
		{
			return String.format("Updated movie originalName from '%s' to '%s'", entityOld.originalName, entityNew.originalName);
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
		entityOld.originalName = entityNew.originalName;
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
