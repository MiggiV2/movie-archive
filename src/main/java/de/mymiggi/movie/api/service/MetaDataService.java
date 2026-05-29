package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import de.mymiggi.movie.api.entity.metadata.MetaDataResponse;
import de.mymiggi.movie.api.entity.metadata.Title;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class MetaDataService
{
	private static final Logger log = LoggerFactory.getLogger(MetaDataService.class);
	@RestClient
	MetaDataClient metaDataClient;

	@Startup
	void fillMetaData()
	{
		boolean isTest = ConfigUtils.getProfiles().contains("test");
		boolean needFill = MovieMetaData.count() == 0 && MovieEntity.count() > 0 && !isTest;
		if (needFill)
		{
			for (PanacheEntityBase entityBase : MovieEntity.listAll())
			{
				MovieEntity movieEntity = (MovieEntity)entityBase;
				Optional<MovieMetaData> metaData = MovieMetaData.find("movieEntity", movieEntity).firstResultOptional();
				if (metaData.isPresent())
				{
					log.info("Skipping {}", movieEntity.name);
					continue;
				}
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					throw new RuntimeException(e);
				}
				System.out.print(".");
				metaData = getMetaData(movieEntity, true);
				try
				{
					QuarkusTransaction.begin();
					metaData.ifPresent(m -> m.persist());
					QuarkusTransaction.commit();
				}
				catch (Exception e)
				{
					log.error(e.getMessage());
				}
			}
		}
	}

	public Optional<MovieMetaData> getMetaData(MovieEntity movie)
	{
		return getMetaData(movie, true);
	}

	public Optional<MovieMetaData> getMetaData(MovieEntity movie, boolean forceSimpleSearch)
	{
		MetaDataResponse metaDataResponse = forceSimpleSearch ? null : metaDataClient.advancedSearch(movie.name, movie.year, movie.year + 1);
		boolean isEmpty = metaDataResponse == null || metaDataResponse.titles == null;
		if (isEmpty)
		{
			metaDataResponse = metaDataClient.search(movie.name, 1);
		}
		isEmpty = metaDataResponse.titles == null;
		if (isEmpty)
		{
			return Optional.empty();
		}
		MovieMetaData metaData = new MovieMetaData(metaDataResponse.titles.getFirst());
		metaData.setMovieEntity(movie);
		return Optional.of(metaData);
	}

	public Optional<Title> getMetaDataById(String id)
	{
		try
		{
			return Optional.of(this.metaDataClient.getById(id));
		}
		catch (Exception e)
		{
			log.error("Failed to get meta data by id {}", id, e);
			return Optional.empty();
		}
	}
}
