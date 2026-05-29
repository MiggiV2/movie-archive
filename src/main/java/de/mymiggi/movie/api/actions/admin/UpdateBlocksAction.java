package de.mymiggi.movie.api.actions.admin;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UpdateBlocksAction
{
	private static final int BLOCKS_PER_LEVEL = 4;
	private Map<String, Integer> levelBlockHeightMap = Map.of(
		"A", 30,
		"B", 40,
		"C", 30,
		"D", 30
	);

	public long updateAllBlocks()
	{
		boolean isTest = ConfigUtils.getProfiles().contains("test");
		if (isTest)
		{
			levelBlockHeightMap = Map.of(
				"A", 4,
				"B", 5,
				"C", 4,
				"D", 5
			);
		}

		PanacheQuery<MovieEntity> movieQuery = MovieEntity.findAll(Sort.ascending("name"));
		movieQuery.page(Page.ofSize(25));

		long updated = 0;
		List<MovieEntity> movies = movieQuery.list();
		updateMoviePage(updated, movies);
		updated += movies.size();

		while (movieQuery.hasNextPage())
		{
			movies = movieQuery.nextPage().list();
			updateMoviePage(updated, movies);
			updated += movies.size();
		}

		return updated;
	}

	private void updateMoviePage(long updated, List<MovieEntity> movies)
	{
		long index = updated;
		for (MovieEntity movie : movies)
		{
			movie.block = getNewBlock(index++);
			movie.persist();
		}
	}

	/**
	 * The index has to start at 0
	 **/
	protected String getNewBlock(long index)
	{
		int maxIndexForLevel = 0;
		for (String level : levelBlockHeightMap.keySet().stream().sorted().toList())
		{
			int blockHeight = levelBlockHeightMap.get(level);
			long levelIndex = index - maxIndexForLevel;
			maxIndexForLevel += blockHeight * BLOCKS_PER_LEVEL;

			if (index >= maxIndexForLevel)
			{
				continue;
			}

			long multipleOfHeight = levelIndex - levelIndex % blockHeight;
			long block = (multipleOfHeight / blockHeight) + 1;
			return level + block;
		}
		return null;
	}
}
