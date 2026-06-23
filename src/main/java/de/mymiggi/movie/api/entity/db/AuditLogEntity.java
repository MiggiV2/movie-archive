package de.mymiggi.movie.api.entity.db;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
import de.mymiggi.movie.api.entity.AuditLogType;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

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

	/**
	 * Audit entry not tied to a movie (e.g. API token lifecycle events).
	 */
	public AuditLogEntity(String username, AuditLogType auditLogType, String message)
	{
		this.userName = username;
		this.auditLogType = auditLogType.toString();
		this.message = message;
		this.movieEntityID = 0;
		this.date = LocalDateTime.now();
	}

	public AuditLogEntity(String username, AbstractAuditLogAction action, String message, MovieEntity movieEntity)
	{
		this.userName = username;
		this.auditLogType = action.getLogType().toString();
		this.message = message;
		this.movieEntityID = movieEntity.id;
		this.date = LocalDateTime.now();
	}
}
