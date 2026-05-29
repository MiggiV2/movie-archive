package de.mymiggi.movie.api.ui;

import de.mymiggi.movie.api.actions.user.GetMovieByIDAction;
import de.mymiggi.movie.api.actions.user.GetSortedMoviesAction;
import de.mymiggi.movie.api.actions.user.SearchAction;
import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("ui/movies")
public class MoviePageResource
{
	@Inject
	GetSortedMoviesAction sortedMoviesAction;
	@Inject
	SearchAction searchAction;
	@Inject
	GetMovieByIDAction getMovieByIDAction;
	@Inject
	DefaultPage defaultPage;
	@Inject
	UiSession session;

	@CheckedTemplate
	public static class Templates
	{
		public static native TemplateInstance list(List<MoviePreview> movies, int page, int displayPage,
			int prevPage, int nextPage, boolean hasNext, String sort, boolean desc, boolean toggleDesc,
			boolean isAdmin, String username);

		// Cards-only fragment (htmx search result swap)
		public static native TemplateInstance list$cards(List<MoviePreview> movies, boolean isAdmin);

		public static native TemplateInstance detail(DetailedMovie movie, boolean isAdmin);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance list(
		@QueryParam("page") @DefaultValue("0") int page,
		@QueryParam("sort") @DefaultValue("name") String sort,
		@QueryParam("desc") @DefaultValue("false") boolean desc)
	{
		List<MoviePreview> movies = "year".equals(sort)
			? sortedMoviesAction.previewsByYear(page, desc, defaultPage)
			: sortedMoviesAction.previewsByName(page, desc, defaultPage);
		long totalPages = Math.ceilDiv(MovieEntity.count(), defaultPage.Size());
		boolean hasNext = (page + 1) < totalPages;
		return Templates.list(movies, page, page + 1, page - 1, page + 1, hasNext, sort, desc, !desc,
			session.isAdmin(), session.getUsername());
	}

	@GET
	@Path("search")
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance search(@QueryParam("query") String query)
	{
		List<MoviePreview> movies = (query == null || query.isBlank())
			? sortedMoviesAction.previewsByName(0, false, defaultPage)
			: searchAction.run(query);
		return Templates.list$cards(movies, session.isAdmin());
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance detail(@PathParam("id") long id)
	{
		DetailedMovie movie = getMovieByIDAction.run(id);
		return Templates.detail(movie, session.isAdmin());
	}
}
