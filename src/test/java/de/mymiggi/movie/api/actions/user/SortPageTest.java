package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
// Add to config:
// %test.quarkus.flyway.locations=filesystem:src/main/resources/dev-db/migration,filesystem:src/main/resources/db/migration
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
		Set<Long> seenIds = new java.util.HashSet<>();

		// when
		while (true)
		{
			List<MoviePreview> currentPage = getSortedMoviesAction.previewsByYear(page, desc, mockedConfig);
			if (currentPage.isEmpty())
			{
				break;
			}

			for (MoviePreview movie : currentPage)
			{
				if (seenIds.contains(movie.getId()))
				{
					fail("Duplicate found: " + movie.getId() + " - " + movie.getTitle());
				}
				seenIds.add(movie.getId());
			}
			page++;
		}
	}
}
