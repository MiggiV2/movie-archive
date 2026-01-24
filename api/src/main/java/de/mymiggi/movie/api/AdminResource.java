package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.DeleteMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateBlocksAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.actions.auditlog.GetAuditLogAction;
import de.mymiggi.movie.api.actions.user.AddTagsAction;
import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
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

@Path("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Deprecated
public class AdminResource
{
	private final AddMovieAction addMovieAction;
	private final UpdateMovieAction updateMovieAction;
	private final DeleteMovieAction deleteMovieAction;
	private final GetAuditLogAction getAuditLogAction;
	private final AddTagsAction addTagsAction;
	private final UpdateBlocksAction updateBlocksAction;

	@Inject
	public AdminResource(AddMovieAction addMovieAction, UpdateMovieAction updateMovieAction,
		DeleteMovieAction deleteMovieAction, GetAuditLogAction getAuditLogAction, AddTagsAction addTagsAction,
		UpdateBlocksAction updateBlocksAction
	)
	{
		this.addMovieAction = addMovieAction;
		this.updateMovieAction = updateMovieAction;
		this.deleteMovieAction = deleteMovieAction;
		this.getAuditLogAction = getAuditLogAction;
		this.addTagsAction = addTagsAction;
		this.updateBlocksAction = updateBlocksAction;
	}

	@POST
	@Path("add-movie")
	@Transactional
	public DetailedMovie addMovie(DetailedMovie movieEntity)
	{
		return addMovieAction.run(movieEntity);
	}

	@PUT
	@Path("update-movie")
	@Transactional
	public DetailedMovie updateMovieById(DetailedMovie movieEntity)
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

	@POST
	@Path("trigger/block-update")
	@Transactional
	public void updateAllBlocks()
	{
		updateBlocksAction.updateAllBlocks();
	}
}