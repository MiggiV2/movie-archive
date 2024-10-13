package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExportServiceTest
{
	public static final MovieEntity MOVIE = new MovieEntity(2020, "Foobar the movie", "B1",
		"https://wikipedia.org/foobar", "BD");

	private static ExportService getExportService()
	{
		ExportService exportService = new ExportService()
		{
			@Override
			protected List<PanacheEntityBase> getAllMovies()
			{
				return List.of(MOVIE);
			}
		};
		return exportService;
	}

	@Test
	void getMovieCSV()
	{
		// given
		ExportService exportService = getExportService();
		String session = exportService.createOneTimeSession();

		// when
		String csv = exportService.getMovieCSV(session);

		// then
		assertEquals(2, csv.split("\n").length);
	}

	@Test
	void getMovieCSV401()
	{
		// given
		ExportService exportService = getExportService();

		// when
		assertThrows(UnauthorizedException.class, () -> exportService.getMovieCSV("foobar"));
	}

	@Test
	@DisplayName("test csv length")
	void movieToCSVLength()
	{
		// when
		String csv = ExportService.movieToCSV(MOVIE);

		// then
		assertEquals(72, csv.length());
	}

	@Test
	@DisplayName("test csv commas")
	void movieToCSVComma()
	{
		// when
		String csv = ExportService.movieToCSV(MOVIE);

		// then
		assertEquals(7, csv.split(",").length);
	}
}