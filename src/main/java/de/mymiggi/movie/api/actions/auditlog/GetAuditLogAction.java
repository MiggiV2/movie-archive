package de.mymiggi.movie.api.actions.auditlog;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GetAuditLogAction
{
	@Inject
	DefaultPage defaultPage;

	public List<AuditLogEntity> run(int page)
	{
		PanacheQuery<AuditLogEntity> movieArchive = AuditLogEntity.findAll(Sort.descending("date"));
		movieArchive.page(Page.ofSize(defaultPage.Size()));
		if (movieArchive.pageCount() <= page || page < 0)
		{
			return new ArrayList<>();
		}
		return movieArchive.page(page, defaultPage.Size()).list();
	}

	public long run()
	{
		long count = getCount();
		long size = defaultPage.Size();
		return Math.ceilDiv(count, size);
	}

	// visible for mocking
	public long getCount()
	{
		return AuditLogEntity.count();
	}
}