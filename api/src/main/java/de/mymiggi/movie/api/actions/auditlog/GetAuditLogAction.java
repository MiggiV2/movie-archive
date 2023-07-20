package de.mymiggi.movie.api.actions.auditlog;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

public class GetAuditLogAction
{
	public Response run(int page, DefaultPage defaultPage)
	{
		PanacheQuery<AuditLogEntity> movieArchive = AuditLogEntity.findAll(Sort.descending("date"));
		movieArchive.page(Page.ofSize(defaultPage.Size()));
		return movieArchive.pageCount() <= page || page < 0
			? Response.status(Status.NOT_FOUND).build()
			: Response.ok(movieArchive.page(page, defaultPage.Size()).list()).build();
	}
}
