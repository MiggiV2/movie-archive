package de.mymiggi.movie.api.actions.user;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

public class GetSortedMoviesAction
{
	public Response runByYear(int page, boolean desc, DefaultPage defaultPage)
	{
		PanacheQuery<MovieEntity> movieArchive = desc
			? MovieEntity.findAll(Sort.descending("year"))
			: MovieEntity.findAll(Sort.ascending("year"));
		return run(movieArchive, page, defaultPage);
	}

	public Response runByName(int page, boolean desc, DefaultPage defaultPage)
	{
		PanacheQuery<MovieEntity> movieArchive = desc
			? MovieEntity.findAll(Sort.descending("name"))
			: MovieEntity.findAll(Sort.ascending("name"));
		return run(movieArchive, page, defaultPage);
	}

	private Response run(PanacheQuery<MovieEntity> movieArchive, int page, DefaultPage defaultPage)
	{
		movieArchive.page(Page.ofSize(defaultPage.Size()));
		return movieArchive.pageCount() <= page || page < 0
			? Response.status(Status.NOT_FOUND).entity("").build()
			: Response.ok(movieArchive.page(page, defaultPage.Size()).list()).build();
	}
}
