package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.DeleteMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.actions.auditlog.GetAuditLogAction;
import de.mymiggi.movie.api.actions.user.AddTagsAction;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import de.mymiggi.movie.api.entity.db.MovieEntity;
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
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("movie-archive/admin")
@RolesAllowed("movie_admins@sso.mymiggi.de")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource
{
	AddMovieAction addMovieAction;
	UpdateMovieAction updateMovieAction;
	DeleteMovieAction deleteMovieAction;
	GetAuditLogAction getAuditLogAction;
	AddTagsAction addTagsAction;

	@Inject
	public AdminResource(AddMovieAction addMovieAction, UpdateMovieAction updateMovieAction,
		DeleteMovieAction deleteMovieAction, GetAuditLogAction getAuditLogAction, AddTagsAction addTagsAction)
	{
		this.addMovieAction = addMovieAction;
		this.updateMovieAction = updateMovieAction;
		this.deleteMovieAction = deleteMovieAction;
		this.getAuditLogAction = getAuditLogAction;
		this.addTagsAction = addTagsAction;
	}

	@POST
	@Path("add-movie")
	@Transactional
	public MovieEntity addMovie(MovieEntity movieEntity)
	{
		return addMovieAction.run(movieEntity);
	}

	@PUT
	@Path("update-movie")
	@Transactional
	public MovieEntity updateMovieById(MovieEntity movieEntity)
	{
		return updateMovieAction.run(movieEntity);
	}

	@DELETE
	@Path("delete-movie")
	@Transactional
	public void deleteMovieByID(@QueryParam("id") Long id)
	{
		deleteMovieAction.run(id);
	}

	@GET
	@Path("auditlog")
	public List<AuditLogEntity> getAuditLog(@QueryParam("page") int page)
	{
		return getAuditLogAction.run(page);
	}

	@GET
	@Path("auditlog-page-count")
	// returns 0 for first page
	public long getAuditLogPageCount()
	{
		return getAuditLogAction.run();
	}

	@POST
	@Path("tag-movie/{movie-id}")
	@Transactional
	public void addTag(@PathParam("movie-id") Long movieId, String[] tags)
	{
		addTagsAction.run(movieId, tags);
	}
}