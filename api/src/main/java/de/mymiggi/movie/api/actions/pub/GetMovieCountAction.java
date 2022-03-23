package de.mymiggi.movie.api.actions.pub;

import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.entity.db.MovieEntity;

public class GetMovieCountAction
{
	public Response run()
	{
		return Response.ok(MovieEntity.count()).build();
	}
}
