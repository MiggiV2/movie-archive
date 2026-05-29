package de.mymiggi.movie.api.actions.auditlog;

import de.mymiggi.movie.api.entity.AuditLogType;

public abstract class AbstractAuditLogAction
{
	public abstract AuditLogType getLogType();
}
