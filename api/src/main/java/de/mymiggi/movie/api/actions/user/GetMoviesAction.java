package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GetMoviesAction
{
	public List<MovieEntity> run(int page, DefaultPage defaultPage)
	{
		PanacheQuery<MovieEntity> movieArchive = MovieEntity.findAll();
		movieArchive.page(Page.ofSize(defaultPage.Size()));
		if (movieArchive.pageCount() <= page || page < 0)
		{
			return new ArrayList<>();
		}
		return movieArchive.page(page, defaultPage.Size()).list();
	}
}
