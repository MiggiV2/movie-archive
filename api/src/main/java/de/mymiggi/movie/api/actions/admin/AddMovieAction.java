package de.mymiggi.movie.api.actions.admin;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
import de.mymiggi.movie.api.actions.auditlog.SaveAuditLogAction;
import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.oidc.UserInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class AddMovieAction extends AbstractAuditLogAction
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
		String firstLetter = String.valueOf(movieEntity.name.charAt(0));
		movieEntity.uuid = firstLetter + MovieEntity.startWith(firstLetter).size();
		movieEntity.persist();
		new SaveAuditLogAction().run(userInfo, this, String.format("Added movie '%s'", movieEntity.name), movieEntity);
		return movieEntity;
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
