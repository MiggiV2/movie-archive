package de.mymiggi.movie.api.entity.db;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
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

	public AuditLogEntity(String username, AbstractAuditLogAction action, String message, MovieEntity movieEntity)
	{
		this.userName = username;
		this.auditLogType = action.getLogType().toString();
		this.message = message;
		this.movieEntityID = movieEntity.id;
		this.date = LocalDateTime.now();
	}
}
