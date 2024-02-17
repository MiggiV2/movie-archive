package de.mymiggi.movie.api.actions.user;

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
	public List<MovieEntity> runByYear(int page, boolean desc, DefaultPage defaultPage)
	{
		PanacheQuery<MovieEntity> movieArchive = desc
			? MovieEntity.findAll(Sort.descending("year"))
			: MovieEntity.findAll(Sort.ascending("year"));
		return run(movieArchive, page, defaultPage);
	}

	public List<MovieEntity> runByName(int page, boolean desc, DefaultPage defaultPage)
	{
		PanacheQuery<MovieEntity> movieArchive = desc
			? MovieEntity.findAll(Sort.descending("name"))
			: MovieEntity.findAll(Sort.ascending("name"));
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
