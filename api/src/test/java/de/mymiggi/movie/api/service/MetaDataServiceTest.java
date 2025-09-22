package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MetaDataServiceTest
{
	@Inject
	MetaDataService metaDataService;

	@Test
	@Disabled
	void searchMovie()
	{
		// Arrival - 2016
		MovieEntity movieEntity = MovieEntity.findById(34);
		assertNotNull(movieEntity);
		assertEquals("Arrival", movieEntity.name);
		Optional<MovieMetaData> metaData = metaDataService.getMetaData(movieEntity);

		assertTrue(metaData.isPresent());
		MovieMetaData movieMetaData = metaData.get();
		assertEquals("tt2543164", movieMetaData.getImdbId());
		assertEquals(34, movieMetaData.getMovieEntity().id);
	}

	@Test
	@Transactional
	void persistMetaData()
	{
		// Arrival - 2016
		MovieEntity movieEntity = MovieEntity.findById(34);
		assertNotNull(movieEntity);
		Optional<MovieMetaData> metaData = metaDataService.getMetaData(movieEntity);

		assertTrue(metaData.isPresent());
		metaData.get().persist();
		assertEquals(3, MovieMetaData.count());
	}
}
