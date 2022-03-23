package de.mymiggi.movie.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.security.identity.SecurityIdentity;

@Path("movie-archive/admin")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AdminResource
{
	@Inject
	SecurityIdentity identity;

	@PUT
	@Path("add-movie")
	@Transactional
	public Response addMovie(MovieEntity movieEntity)
	{
		return new AddMovieAction().run(movieEntity);
	}

	@PUT
	@Path("update-movie-by-id")
	@Transactional
	public Response updateMovieById(MovieEntity movieEntity)
	{
		return new UpdateMovieAction().run(movieEntity);
	}
}
