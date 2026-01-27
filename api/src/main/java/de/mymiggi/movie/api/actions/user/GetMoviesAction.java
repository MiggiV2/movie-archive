package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GetMoviesAction
{
	static MoviePreview enrichMovie(MovieEntity movieEntity)
	{
		PanacheQuery<MovieMetaData> movieMetaData = MovieMetaData.find("movieEntity", movieEntity);
		return new MoviePreview(movieEntity, movieMetaData.firstResultOptional());
	}

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
