package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
public class SortPageTest
{
	@Inject
	GetSortedMoviesAction getSortedMoviesAction;

	@Test
	void shouldNotContainDuplicates()
	{
		// given
		int page = 0;
		boolean desc = true;
		DefaultPage mockedConfig = () -> 30;

		// when
		List<MoviePreview> firstPage = getSortedMoviesAction.previewsByYear(page, desc, mockedConfig);
		List<MoviePreview> secondPage = getSortedMoviesAction.previewsByYear(page + 1, desc, mockedConfig);

		// then
		List<MoviePreview> duplicates = firstPage.stream()
			.filter(movie -> secondPage.stream().anyMatch(m -> m.getId() == movie.getId()))
			.toList();
		if (!duplicates.isEmpty())
		{
			duplicates.forEach(dup -> System.err.println("Duplicate found: " + dup.getId() + " - " + dup.getTitle()));
			fail("Duplicates found between pages!");
		}
	}
}
