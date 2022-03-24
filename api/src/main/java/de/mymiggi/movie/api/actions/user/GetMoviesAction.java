package de.mymiggi.movie.api.actions.user;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

public class GetMoviesAction
{
	public Response run(int page, DefaultPage defaultPage)
	{
		PanacheQuery<MovieEntity> movieArchive = MovieEntity.findAll();
		movieArchive.page(Page.ofSize(defaultPage.Size()));
		return movieArchive.pageCount() <= page || page < 0
			? Response.status(Status.NOT_FOUND).entity("").build()
			: Response.ok(movieArchive.page(page, defaultPage.Size()).list()).build();
	}
}
