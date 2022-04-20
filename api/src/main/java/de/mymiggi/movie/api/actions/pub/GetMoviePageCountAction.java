package de.mymiggi.movie.api.actions.pub;

import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class GetMoviePageCountAction
{
	public Response run(DefaultPage defaultPage)
	{
		double max = MovieEntity.count() / defaultPage.Size();
		return Response.ok((int)Math.nextUp(max)).build();
	}
}
