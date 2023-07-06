package de.mymiggi.movie.api.entity;

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
		if (movie.name.equals(query))
		{
			this.matchesQuery = 100;
		}
		else if (movie.name.contains(query))
		{
			double percent = (double)query.length() / (double)movie.name.length();
			this.matchesQuery = (int)(percent * 100);
		}
		else
		{
			this.matchesQuery = analyzeWords(movie, query);
		}
	}

	private int analyzeWords(MovieEntity movie, String query)
	{
		int hits = 0;
		Set<String> words = Set.of(query.split(" "));
		for (String w : words)
		{
			if (movie.name.toLowerCase().contains(w))
			{
				hits++;
			}
		}
		return Math.min(hits, 50);
	}

	@Override
	public int compareTo(SearchWrapper other)
	{
		return Integer.compare(this.matchesQuery, other.getMatchesQuery());
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
