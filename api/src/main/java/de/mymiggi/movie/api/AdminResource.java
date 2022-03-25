package de.mymiggi.movie.api;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.DeleteMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.security.identity.SecurityIdentity;

@Path("movie-archive/admin")
@RolesAllowed("admin")
@ApplicationScoped
public class AdminResource
{
	@Inject
	SecurityIdentity identity;

	@POST
	@Path("add-movie")
	@Transactional
	public Response addMovie(MovieEntity movieEntity)
	{
		return new AddMovieAction().run(movieEntity);
	}

	@PUT
	@Path("update-movie")
	@Transactional
	public Response updateMovieById(MovieEntity movieEntity)
	{
		return new UpdateMovieAction().run(movieEntity);
	}

	@DELETE
	@Path("delete-movie")
	@Transactional
	public Response deleteMovieByID(@QueryParam("id") Long id)
	{
		return new DeleteMovieAction().run(id);
	}
}
