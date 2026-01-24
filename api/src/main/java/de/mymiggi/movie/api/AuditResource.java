package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.auditlog.GetAuditLogAction;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("audit")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuditResource
{
	@Inject
	GetAuditLogAction getAuditLogAction;

	@GET
	public List<AuditLogEntity> getAuditLog(@QueryParam("page") int page)
	{
		return getAuditLogAction.run(page);
	}

	@GET
	@Path("pages")
	@Produces(MediaType.TEXT_PLAIN)
	// returns 0 for first page
	public long getAuditLogPageCount()
	{
		return getAuditLogAction.run();
	}
}
