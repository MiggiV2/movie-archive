package de.mymiggi.movie.api.actions.auditlog;

import jakarta.ws.rs.core.Response;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;

public class GetAuditLogPageCountAction
{
	public Response run(DefaultPage defaultPage)
	{
		double max = AuditLogEntity.count() / defaultPage.Size();
		return Response.ok((int)Math.nextUp(max)).build();
	}
}
