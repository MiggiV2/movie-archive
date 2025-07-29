package de.mymiggi.movie.api.entity.db;

import de.mymiggi.movie.api.entity.metadata.Title;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class MovieMetaData
{
	@Id
	private String imdbId;
	private String primaryImage;
	private int year;
	private int runtime;
	private List<String> genres;
	private double rating;

	public MovieMetaData(Title title)
	{
		this.imdbId = title.id;
		this.primaryImage = title.primaryImage.url;
		this.year = title.startYear;
		this.runtime = title.runtimeSeconds;
		this.genres = title.genres;
		this.rating = title.rating.aggregateRating;
	}

	// Used by jackson
	public MovieMetaData()
	{
	}

	public String getImdbId()
	{
		return imdbId;
	}

	public void setImdbId(String imdbId)
	{
		this.imdbId = imdbId;
	}

	public String getPrimaryImage()
	{
		return primaryImage;
	}

	public void setPrimaryImage(String primaryImage)
	{
		this.primaryImage = primaryImage;
	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public int getRuntime()
	{
		return runtime;
	}

	public void setRuntime(int runtime)
	{
		this.runtime = runtime;
	}

	public List<String> getGenres()
	{
		return genres;
	}

	public void setGenres(List<String> genres)
	{
		this.genres = genres;
	}

	public double getRating()
	{
		return rating;
	}

	public void setRating(double rating)
	{
		this.rating = rating;
	}
}
