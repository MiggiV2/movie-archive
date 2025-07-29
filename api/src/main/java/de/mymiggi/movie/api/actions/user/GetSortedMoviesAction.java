package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GetSortedMoviesAction
{
	private static PanacheQuery<MovieEntity> getQuery(boolean desc, String columns)
	{
		return desc
			? MovieEntity.findAll(Sort.descending(columns))
			: MovieEntity.findAll(Sort.ascending(columns));
	}

	public List<MoviePreview> previewsByYear(int page, boolean desc, DefaultPage defaultPage)
	{
		return runByYear(page, desc, defaultPage).stream()
			.map(GetMoviesAction::enrichMovie)
			.toList();
	}

	public List<MoviePreview> previewsByName(int page, boolean desc, DefaultPage defaultPage)
	{
		return runByName(page, desc, defaultPage).stream()
			.map(GetMoviesAction::enrichMovie)
			.toList();
	}

	public List<MovieEntity> runByYear(int page, boolean desc, DefaultPage defaultPage)
	{
		PanacheQuery<MovieEntity> movieArchive = getQuery(desc, "year");
		return run(movieArchive, page, defaultPage);
	}

	public List<MovieEntity> runByName(int page, boolean desc, DefaultPage defaultPage)
	{
		PanacheQuery<MovieEntity> movieArchive = getQuery(desc, "name");
		return run(movieArchive, page, defaultPage);
	}

	private List<MovieEntity> run(PanacheQuery<MovieEntity> movieArchive, int page, DefaultPage defaultPage)
	{
		movieArchive.page(Page.ofSize(defaultPage.Size()));
		if (movieArchive.pageCount() <= page || page < 0)
		{
			return new ArrayList<>();
		}
		return movieArchive.page(page, defaultPage.Size()).list();
	}
}
