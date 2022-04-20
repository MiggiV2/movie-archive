package de.mymiggi.movie.api.actions.auditlog;

import org.jboss.logging.Logger;

import de.mymiggi.movie.api.entity.KeycloakUser;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class SaveAuditLogAction
{
	private static final Logger LOG = Logger.getLogger(SaveAuditLogAction.class.getSimpleName());

	public void run(KeycloakUser user, AbstractAuditLogAction action, String message, MovieEntity movieEntity)
	{
		AuditLogEntity logEntity = new AuditLogEntity(user, action, message, movieEntity);
		LOG.info(String.format("User: %s - '%s'", user.getUserName(), message));
		logEntity.persist();
	}
}
