package de.mymiggi.movie.api.actions.auditlog;

import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.oidc.UserInfo;
import org.jboss.logging.Logger;

public class SaveAuditLogAction
{
	private static final Logger LOG = Logger.getLogger(SaveAuditLogAction.class.getSimpleName());

	public void run(UserInfo user, AbstractAuditLogAction action, String message, MovieEntity movieEntity)
	{
		AuditLogEntity logEntity = new AuditLogEntity(user.getName(), action, message, movieEntity);
		LOG.info(String.format("User: %s - '%s'", user.getName(), message));
		logEntity.persist();
	}

	public void run(String username, AuditLogType auditLogType, String message)
	{
		AuditLogEntity logEntity = new AuditLogEntity(username, auditLogType, message);
		LOG.info(String.format("User: %s - '%s'", username, message));
		logEntity.persist();
	}
}
