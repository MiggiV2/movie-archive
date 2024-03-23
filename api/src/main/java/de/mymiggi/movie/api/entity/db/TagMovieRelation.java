package de.mymiggi.movie.api.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class TagMovieRelation extends PanacheEntity
{
	@ManyToOne
	private MovieEntity movie;
	@ManyToOne
	private TagEntity tag;

	public TagMovieRelation()
	{
	}

	public TagMovieRelation(MovieEntity movie, TagEntity tag)
	{
		this.movie = movie;
		this.tag = tag;
	}

	public MovieEntity getMovie()
	{
		return movie;
	}

	public void setMovie(MovieEntity movie)
	{
		this.movie = movie;
	}

	public TagEntity getTag()
	{
		return tag;
	}

	public void setTag(TagEntity tag)
	{
		this.tag = tag;
	}
}
