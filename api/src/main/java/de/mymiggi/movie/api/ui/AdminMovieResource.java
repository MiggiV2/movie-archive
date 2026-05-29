package de.mymiggi.movie.api.ui;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.DeleteMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.actions.user.GetMovieByIDAction;
import de.mymiggi.movie.api.entity.DetailedMovie;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;

@Path("ui/admin/movies")
public class AdminMovieResource
{
	@Inject
	AddMovieAction addMovieAction;
	@Inject
	UpdateMovieAction updateMovieAction;
	@Inject
	DeleteMovieAction deleteMovieAction;
	@Inject
	GetMovieByIDAction getMovieByIDAction;
	@Inject
	UiSession session;

	@CheckedTemplate
	public static class Templates
	{
		public static native TemplateInstance form(DetailedMovie movie, boolean isNew, boolean isAdmin, String username);
	}

	@GET
	@Path("new")
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance newForm()
	{
		return Templates.form(new DetailedMovie(), true, session.isAdmin(), session.getUsername());
	}

	@GET
	@Path("{id}/edit")
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance editForm(@PathParam("id") long id)
	{
		return Templates.form(getMovieByIDAction.run(id), false, session.isAdmin(), session.getUsername());
	}

	@POST
	@Transactional
	@Produces(MediaType.TEXT_HTML)
	public Response add(
		@RestForm String title,
		@RestForm String type,
		@RestForm int year,
		@RestForm String block,
		@RestForm String wikiUrl,
		@RestForm String originalName,
		@RestForm String externalId)
	{
		addMovieAction.run(build(0, title, type, year, block, wikiUrl, originalName, externalId));
		return redirectToList();
	}

	@PUT
	@Path("{id}")
	@Transactional
	@Produces(MediaType.TEXT_HTML)
	public Response update(
		@PathParam("id") long id,
		@RestForm String title,
		@RestForm String type,
		@RestForm int year,
		@RestForm String block,
		@RestForm String wikiUrl,
		@RestForm String originalName,
		@RestForm String externalId)
	{
		updateMovieAction.run(build(id, title, type, year, block, wikiUrl, originalName, externalId));
		return redirectToList();
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public Response delete(@PathParam("id") Long id)
	{
		deleteMovieAction.run(id);
		return redirectToList();
	}

	private DetailedMovie build(long id, String title, String type, int year, String block,
		String wikiUrl, String originalName, String externalId)
	{
		DetailedMovie movie = new DetailedMovie();
		movie.setId(id);
		movie.setTitle(title);
		movie.setType(type);
		movie.setYear(year);
		movie.setBlock(block);
		movie.setWikiUrl(wikiUrl);
		movie.setOriginalName(originalName);
		movie.setExternalId(externalId);
		return movie;
	}

	// Tell htmx to navigate the browser back to the grid after a successful mutation.
	private Response redirectToList()
	{
		return Response.ok().header("HX-Redirect", "/ui/movies").build();
	}
}
