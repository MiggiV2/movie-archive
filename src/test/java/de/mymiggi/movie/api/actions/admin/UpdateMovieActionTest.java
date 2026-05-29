package de.mymiggi.movie.api.actions.admin;

import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import de.mymiggi.movie.api.service.MetaDataService;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UpdateMovieActionTest
{
	@Inject
	UpdateMovieAction updateMovieAction;

	@InjectMock
	MetaDataService metaDataService;

	@Test
	void testUpdateNull()
	{
		assertThrows(BadRequestException.class, () -> updateMovieAction.run(null));
	}

	@Test
	void testUpdateInvalid()
	{
		DetailedMovie movie = new DetailedMovie();
		assertThrows(BadRequestException.class, () -> updateMovieAction.run(movie));
	}

	@Test
	void testUpdateNotFound()
	{
		DetailedMovie movie = new DetailedMovie();
		movie.setId(999999L);
		movie.setTitle("Title");
		movie.setType("Type");
		movie.setYear(2023);
		
		assertThrows(NotFoundException.class, () -> updateMovieAction.run(movie));
	}

	@Test
	@Transactional
	void testUpdateSuccess()
	{
		// 278 is Oppenheimer from test data
		MovieEntity entity = MovieEntity.findById(278L);
		assertNotNull(entity);
		
		DetailedMovie update = new DetailedMovie(entity, new MovieMetaData());
		update.setTitle("Oppenheimer Updated");
		update.setWikiUrl("https://new.url");
		update.setBlock("Block Z");
		update.setYear(2024);
		update.setOriginalName("Oppenheimer Orig");
		
		DetailedMovie result = updateMovieAction.run(update);
		
		assertEquals("Oppenheimer Updated", result.getTitle());
		assertEquals("https://new.url", result.getWikiUrl());
		assertEquals("Block Z", result.getBlock());
		assertEquals(2024, result.getYear());
		assertEquals("Oppenheimer Orig", result.getOriginalName());
		
		QuarkusTransaction.setRollbackOnly();
	}
}
