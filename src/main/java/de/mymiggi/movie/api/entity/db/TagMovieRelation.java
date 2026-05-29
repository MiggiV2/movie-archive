package de.mymiggi.movie.api.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class TagMovieRelation extends PanacheEntityBase
{
	@Id
	@SequenceGenerator(name = "tagMovieRelationSequence", sequenceName = "tag_movie_relation_id_seq", initialValue = 17)
	@GeneratedValue(generator = "tagMovieRelationSequence")
	public Long id;
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
