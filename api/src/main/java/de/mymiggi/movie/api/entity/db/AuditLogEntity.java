package de.mymiggi.movie.api.entity.db;

import java.time.LocalDateTime;

import javax.persistence.Entity;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
import de.mymiggi.movie.api.entity.KeycloakUser;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class AuditLogEntity extends PanacheEntity
{
	public String userName;
	public String auditLogType;
	public String message;
	public long movieEntityID;
	public LocalDateTime date;

	public AuditLogEntity()
	{
	}

	public AuditLogEntity(KeycloakUser user, AbstractAuditLogAction action, String message)
	{
		this.userName = user.getUserName();
		this.auditLogType = action.getLogType().toString();
		this.message = message;
		this.date = LocalDateTime.now();
	}

	public AuditLogEntity(KeycloakUser user, AbstractAuditLogAction action, String message, MovieEntity movieEntity)
	{
		this.userName = user.getUserName();
		this.auditLogType = action.getLogType().toString();
		this.message = message;
		this.movieEntityID = movieEntity.id;
		this.date = LocalDateTime.now();
	}
}
