package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class MetaDataServiceTest
{
	@Inject
	MetaDataService metaDataService;

	@Test
	void searchMovie()
	{
		// Arrival - 2016
		MovieEntity movieEntity = MovieEntity.findById(36L);
		assertNotNull(movieEntity);
		assertEquals("Arrival", movieEntity.name);
		Optional<MovieMetaData> metaData = metaDataService.getMetaData(movieEntity);

		assertTrue(metaData.isPresent());
		MovieMetaData movieMetaData = metaData.get();
		assertEquals("tt2543164", movieMetaData.getImdbId());
		assertEquals(36L, movieMetaData.getMovieEntity().id);
	}

	@Test
	@Transactional
	void persistMetaData()
	{
		// 300 Rise Of The Empire
		MovieEntity movieEntity = MovieEntity.findById(9L);
		assertNotNull(movieEntity);

		// Remove existing metadata to avoid unique constraint violation
		MovieMetaData.delete("movieEntity", movieEntity);

		// Fetch meta data
		Optional<MovieMetaData> metaData = metaDataService.getMetaData(movieEntity);
		assertTrue(metaData.isPresent());

		// Check persistence
		long countBefore = MovieMetaData.count();
		metaData.get().persist();
		long countAfter = MovieMetaData.count();
		assertEquals(countBefore + 1, countAfter);
	}
}
