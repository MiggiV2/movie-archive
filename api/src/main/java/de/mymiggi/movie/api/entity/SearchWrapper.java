package de.mymiggi.movie.api.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.mymiggi.movie.api.entity.db.MovieEntity;

public class SearchWrapper implements Comparable<SearchWrapper>
{
	private MovieEntity movie;
	private int matchesQuery;

	public SearchWrapper(MovieEntity movie, String query)
	{
		this.movie = movie;
		calcRelevance(query);
	}

	public void calcRelevance(String query)
	{
		if (movie.name.equalsIgnoreCase(query))
		{
			this.matchesQuery = 100;
		}
		else
		{
			this.matchesQuery = analyzeWords(movie, query);
		}
	}

	private int analyzeWords(MovieEntity movie, String query)
	{
		int hits = 0;

		Set<String> queryWords = new HashSet<>();
		Collections.addAll(queryWords, query.toLowerCase().split(" "));
		Set<String> nameWords = new HashSet<>();
		Collections.addAll(nameWords, movie.name.toLowerCase().split(" "));

		for (String w : queryWords)
		{
			for (String m : nameWords)
			{
				if (m.equals(w))
				{
					hits += m.length();
				}
				else if (m.contains(w))
				{
					hits++;
				}
			}
		}
		return Math.min(hits, 50);
	}

	@Override
	public int compareTo(SearchWrapper other)
	{
		return Integer.compare(other.getMatchesQuery(), this.matchesQuery);
	}

	public MovieEntity getMovie()
	{
		return movie;
	}

	public void setMovie(MovieEntity movie)
	{
		this.movie = movie;
	}

	public int getMatchesQuery()
	{
		return matchesQuery;
	}

	public void setMatchesQuery(int matchesQuery)
	{
		this.matchesQuery = matchesQuery;
	}
}
