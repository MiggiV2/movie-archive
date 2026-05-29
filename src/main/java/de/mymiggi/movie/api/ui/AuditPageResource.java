package de.mymiggi.movie.api.ui;

import de.mymiggi.movie.api.actions.auditlog.GetAuditLogAction;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("ui/audit")
public class AuditPageResource
{
	@Inject
	GetAuditLogAction getAuditLogAction;
	@Inject
	UiSession session;

	@CheckedTemplate
	public static class Templates
	{
		public static native TemplateInstance audit(List<AuditLogEntity> entries, int displayPage,
			int prevPage, int nextPage, boolean hasPrev, boolean hasNext, boolean isAdmin, String username);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance audit(@QueryParam("page") @DefaultValue("0") int page)
	{
		List<AuditLogEntity> entries = getAuditLogAction.run(page);
		long totalPages = getAuditLogAction.run();
		boolean hasNext = (page + 1) < totalPages;
		return Templates.audit(entries, page + 1, page - 1, page + 1, page > 0, hasNext,
			session.isAdmin(), session.getUsername());
	}
}
