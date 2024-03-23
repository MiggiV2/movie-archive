package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.DeleteMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.actions.auditlog.GetAuditLogAction;
import de.mymiggi.movie.api.entity.KeycloakUser;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
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

import java.util.List;

@Path("movie-archive/admin")
@RolesAllowed("admin")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource
{
	SecurityIdentity identity;
	DefaultPage defaultPage;
	AddMovieAction addMovieAction;
	UpdateMovieAction updateMovieAction;
	DeleteMovieAction deleteMovieAction;
	GetAuditLogAction getAuditLogAction;

	@Inject
	public AdminResource(SecurityIdentity identity, DefaultPage defaultPage, AddMovieAction addMovieAction,
		UpdateMovieAction updateMovieAction, DeleteMovieAction deleteMovieAction, GetAuditLogAction getAuditLogAction)
	{
		this.identity = identity;
		this.defaultPage = defaultPage;
		this.addMovieAction = addMovieAction;
		this.updateMovieAction = updateMovieAction;
		this.deleteMovieAction = deleteMovieAction;
		this.getAuditLogAction = getAuditLogAction;
	}

	@POST
	@Path("add-movie")
	@Transactional
	public MovieEntity addMovie(MovieEntity movieEntity)
	{
		return addMovieAction.run(movieEntity, new KeycloakUser(identity));
	}

	@PUT
	@Path("update-movie")
	@Transactional
	public MovieEntity updateMovieById(MovieEntity movieEntity)
	{
		return updateMovieAction.run(movieEntity, new KeycloakUser(identity));
	}

	@DELETE
	@Path("delete-movie")
	@Transactional
	public void deleteMovieByID(@QueryParam("id") Long id)
	{
		deleteMovieAction.run(id, new KeycloakUser(identity));
	}

	@GET
	@Path("auditlog")
	public List<AuditLogEntity> getAuditLog(@QueryParam("page") int page)
	{
		return getAuditLogAction.run(page, defaultPage);
	}

	@GET
	@Path("auditlog-page-count")
	public long getAuditLogPageCount()
	{
		return getAuditLogAction.run(defaultPage);
	}
}
