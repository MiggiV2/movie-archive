package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.MoviePreview;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SearchActionTest
{
	@Inject
	SearchAction searchAction;

	@Test
	void testSearchWithNullQuery()
	{
		assertThrows(BadRequestException.class, () -> searchAction.run(null));
	}

	@Test
	void testSearchWithEmptyQuery()
	{
		assertThrows(BadRequestException.class, () -> searchAction.run("   "));
	}

	@Test
	void testSearchPrefix()
	{
		// Should find "Oppenheimer"
		List<MoviePreview> results = searchAction.run("Oppen");
		assertFalse(results.isEmpty());
		boolean found = results.stream().anyMatch(m -> m.getTitle().equals("Oppenheimer"));
		assertTrue(found, "Should find Oppenheimer via prefix");
	}

	@Test
	void testSearchInfix()
	{
		// Should find "Oppenheimer"
		List<MoviePreview> results = searchAction.run("enhei");
		assertFalse(results.isEmpty());
		boolean found = results.stream().anyMatch(m -> m.getTitle().equals("Oppenheimer"));
		assertTrue(found, "Should find Oppenheimer via infix");
	}

	@Test
	void testSearchMultiWord()
	{
		// Should find "Oppenheimer" via "Opp" AND "heimer"
		List<MoviePreview> results = searchAction.run("Opp heimer");
		assertFalse(results.isEmpty());
		boolean found = results.stream().anyMatch(m -> m.getTitle().equals("Oppenheimer"));
		assertTrue(found, "Should find Oppenheimer via multi-word");
	}

	@Test
	void testSearchNoMatch()
	{
		List<MoviePreview> results = searchAction.run("ThisMovieDoesNotExist12345");
		assertTrue(results.isEmpty());
	}
}
