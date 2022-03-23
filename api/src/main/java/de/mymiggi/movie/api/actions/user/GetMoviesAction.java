package de.mymiggi.movie.api.actions.user;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

public class GetMoviesAction
{
	private int pageSize = 30;

	public Response run(int page)
	{
		PanacheQuery<MovieEntity> movieArchive = MovieEntity.findAll();
		movieArchive.page(Page.ofSize(pageSize));
		return movieArchive.pageCount() <= page || page < 0
			? Response.status(Status.NOT_FOUND).entity("").build()
			: Response.ok(movieArchive.page(page, pageSize).list()).build();
	}
}
