package de.mymiggi.movie.api.actions.auditlog;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import jakarta.ws.rs.core.Response;

public class GetAuditLogPageCountAction
{
	public Response run(DefaultPage defaultPage)
	{
		double max = ((double)AuditLogEntity.count()) / defaultPage.Size();
		return Response.ok((int)Math.nextUp(max)).build();
	}
}
