package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.metadata.MetaDataResponse;
import de.mymiggi.movie.api.entity.metadata.Title;
import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Global test stub that replaces the live {@link MetaDataClient} REST client
 * (https://api.imdbapi.dev/) in every {@code @QuarkusTest}. The real API is
 * rate limited and occasionally returns 500/404, which made the test suite
 * flaky in CI. This mock returns deterministic metadata instead.
 */
@Mock
@ApplicationScoped
@RestClient
public class MockMetaDataClient implements MetaDataClient
{
	public static final String MOCK_IMDB_ID = "tt0000001";
	public static final String MOCK_TITLE = "Mock Movie";
	public static final int MOCK_YEAR = 2020;

	@Override
	public MetaDataResponse advancedSearch(String query, int startYear, int endYear)
	{
		return response(query);
	}

	@Override
	public MetaDataResponse search(String query, int limit)
	{
		return response(query);
	}

	@Override
	public Title getById(String id)
	{
		return title(MOCK_TITLE);
	}

	private MetaDataResponse response(String query)
	{
		MetaDataResponse response = new MetaDataResponse();
		response.titles = new ArrayList<>(List.of(title(query)));
		response.totalCount = 1;
		return response;
	}

	private Title title(String primaryTitle)
	{
		Title title = new Title();
		title.id = MOCK_IMDB_ID;
		title.type = "movie";
		title.primaryTitle = primaryTitle;
		title.originalTitle = primaryTitle;
		title.startYear = MOCK_YEAR;
		title.runtimeSeconds = 6000;
		title.genres = List.of("Drama");
		return title;
	}
}
