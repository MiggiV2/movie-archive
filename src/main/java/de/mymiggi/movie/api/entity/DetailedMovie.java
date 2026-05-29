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
		this.movie = new MovieEntity();
		this.movieMetaData = new MovieMetaData();
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

	public void setYear(int year)
	{
		this.movie.year = year;
	}

	public String getTitle()
	{
		return this.movie.name;
	}

	public void setTitle(String title)
	{
		this.movie.name = title;
	}

	public String getBlock()
	{
		return this.movie.block;
	}

	public void setBlock(String block)
	{
		this.movie.block = block;
	}

	public String getWikiUrl()
	{
		return this.movie.wikiUrl;
	}

	public void setWikiUrl(String wikiUrl)
	{
		this.movie.wikiUrl = wikiUrl;
	}

	public String getType()
	{
		return this.movie.type;
	}

	public void setType(String type)
	{
		this.movie.type = type;
	}

	public int getRuntime()
	{
		return this.movieMetaData.getRuntime();
	}

	public void setRuntime(int runtime)
	{
		this.movieMetaData.setRuntime(runtime);
	}

	public List<String> getGenres()
	{
		return this.movieMetaData.getGenres();
	}

	public void setGenres(List<String> genres)
	{
		this.movieMetaData.setGenres(genres);
	}

	public double getRating()
	{
		return this.movieMetaData.getRating();
	}

	public void setRating(double rating)
	{
		this.movieMetaData.setRating(rating);
	}

	public String getImage()
	{
		return this.movieMetaData.getPrimaryImage();
	}

	public void setImage(String image)
	{
		this.movieMetaData.setPrimaryImage(image);
	}

	public long getId()
	{
		return this.movie.id;
	}

	public void setId(long id)
	{
		this.movie.id = id;
	}

	public String getOriginalName()
	{
		return this.movie.originalName;
	}

	public void setOriginalName(String originalName)
	{
		this.movie.originalName = originalName;
	}

	public String getExternalId()
	{
		return this.movieMetaData.getImdbId();
	}

	public void setExternalId(String externalId)
	{
		this.movieMetaData.setImdbId(externalId);
	}
}
