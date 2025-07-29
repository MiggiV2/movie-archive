package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class MetaDataServiceTest
{
	@Inject
	MetaDataService metaDataService;

	@Test
	void searchMovie()
	{
		MovieEntity movieEntity = new MovieEntity(2024, "Dune: Part Two", "", "", "");
		Optional<MovieMetaData> metaData = metaDataService.getMetaData(movieEntity);

		assertTrue(metaData.isPresent());
		MovieMetaData movieMetaData = metaData.get();
		assertEquals("tt15239678", movieMetaData.getImdbId());
	}
}
