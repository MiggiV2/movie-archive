package de.mymiggi.movie.api.entity;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;

import java.util.List;

public class DetailedMovie
{
	private MovieEntity movie;
	private MovieMetaData movieMetaData;

	// Used by Jackson
	public DetailedMovie()
	{
	}

	public DetailedMovie(MovieEntity movie, MovieMetaData movieMetaData)
	{
		this.movie = movie;
		this.movieMetaData = movieMetaData;
	}

	public int getYear()
	{
		return this.movie.year;
	}

	public String getTitle()
	{
		return this.movie.name;
	}

	public String getBlock()
	{
		return this.movie.block;
	}

	public String getWikiUrl()
	{
		return this.movie.wikiUrl;
	}

	public String getType()
	{
		return this.movie.type;
	}

	public int getRuntime()
	{
		return this.movieMetaData.getRuntime();
	}

	public List<String> getGenres()
	{
		return this.movieMetaData.getGenres();
	}

	public double getRating()
	{
		return this.movieMetaData.getRating();
	}

	public String getImage()
	{
		return this.movieMetaData.getPrimaryImage();
	}
}
