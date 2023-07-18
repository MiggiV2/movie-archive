package de.mymiggi.movie.api;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.DeleteMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.actions.auditlog.GetAuditLogAction;
import de.mymiggi.movie.api.actions.auditlog.GetAuditLogPageCountAction;
import de.mymiggi.movie.api.entity.KeycloakUser;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.security.identity.SecurityIdentity;

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
