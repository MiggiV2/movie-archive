package de.mymiggi.movie.api.actions.pub;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

@ApplicationScoped
public class GetMoviePageCountAction
{
	public Response run(DefaultPage defaultPage)
	{
		double max = (double)MovieEntity.count() / defaultPage.Size();
		return Response.ok((int)Math.nextUp(max)).build();
	}
}
