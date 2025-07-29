package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import de.mymiggi.movie.api.entity.metadata.MedaDataResponse;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Optional;

@ApplicationScoped
public class MetaDataService
{
	@RestClient
	MetaDataClient metaDataClient;

	public Optional<MovieMetaData> getMetaData(MovieEntity movie)
	{
		MedaDataResponse metaDataResponse = metaDataClient.search(movie.name, movie.year, movie.year + 1);
		if (metaDataResponse == null || metaDataResponse.titles.isEmpty())
		{
			return Optional.empty();
		}
		MovieMetaData metaData = new MovieMetaData(metaDataResponse.titles.getFirst());
		return Optional.of(metaData);
	}
}
