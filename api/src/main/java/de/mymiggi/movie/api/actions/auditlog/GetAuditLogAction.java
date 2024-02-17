package de.mymiggi.movie.api.actions.auditlog;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GetAuditLogAction
{
	public List<AuditLogEntity> run(int page, DefaultPage defaultPage)
	{
		PanacheQuery<AuditLogEntity> movieArchive = AuditLogEntity.findAll(Sort.descending("date"));
		movieArchive.page(Page.ofSize(defaultPage.Size()));
		if (movieArchive.pageCount() <= page || page < 0)
		{
			return new ArrayList<>();
		}
		return movieArchive.page(page, defaultPage.Size()).list();
	}

	public long run(DefaultPage defaultPage)
	{
		return Math.ceilDiv(AuditLogEntity.count(), defaultPage.Size()) - 1;
	}
}
