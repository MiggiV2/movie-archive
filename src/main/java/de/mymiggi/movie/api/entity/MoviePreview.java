package de.mymiggi.movie.api.entity;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;

import java.util.Optional;

public class MoviePreview
{
	private String title;
	private int year;
	private String image;
	private long id;

	public MoviePreview(MovieEntity movieEntity, Optional<MovieMetaData> movieMetaData)
	{
		this.title = movieEntity.name;
		this.year = movieEntity.year;
		this.id = movieEntity.id;
		this.image = movieMetaData.map(MovieMetaData::getPrimaryImage).orElse(null);
	}

	// Used by jackson
	public MoviePreview()
	{
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	public long getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return "MoviePreview{" +
			"title='" + title + '\'' +
			", year=" + year +
			", id=" + id +
			'}';
	}
}
