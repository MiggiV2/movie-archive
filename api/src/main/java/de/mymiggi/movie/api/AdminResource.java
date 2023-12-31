package de.mymiggi.movie.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.DeleteMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.actions.auditlog.GetAuditLogAction;
import de.mymiggi.movie.api.actions.auditlog.GetAuditLogPageCountAction;
import de.mymiggi.movie.api.entity.KeycloakUser;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;

@Path("movie-archive/admin")
@RolesAllowed("admin")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource
{
	@Inject
	SecurityIdentity identity;
	@Inject
	DefaultPage defaultPage;

	@POST
	@Path("add-movie")
	@Transactional
	public Response addMovie(MovieEntity movieEntity)
	{
		return new AddMovieAction().run(movieEntity, new KeycloakUser(identity));
	}

	@PUT
	@Path("update-movie")
	@Transactional
	public Response updateMovieById(MovieEntity movieEntity)
	{
		return new UpdateMovieAction().run(movieEntity, new KeycloakUser(identity));
	}

	@DELETE
	@Path("delete-movie")
	@Transactional
	public Response deleteMovieByID(@QueryParam("id") Long id)
	{
		return new DeleteMovieAction().run(id, new KeycloakUser(identity));
	}

	@GET
	@Path("auditlog")
	public Response getAuditLog(@QueryParam("page") int page)
	{
		return new GetAuditLogAction().run(page, defaultPage);
	}

	@GET
	@Path("auditlog-page-count")
	public Response getAuditLogPageCount()
	{
		return new GetAuditLogPageCountAction().run(defaultPage);
	}
}
