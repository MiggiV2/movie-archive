package de.mymiggi.movie.api;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.TagEntity;
import de.mymiggi.movie.api.entity.db.TagMovieRelation;
import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("tag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagResource
{
	@GET
	@Path("tags")
	public List<TagEntity> listTags()
	{
		return TagEntity.listAll(Sort.by("name"));
	}

	@GET
	@Path("tags/{id}")
	public List<MovieEntity> listTaggedMovies(@PathParam("id") Long tagId)
	{
		TagEntity tag = TagEntity.findById(tagId);
		if (tag == null)
		{
			throw new NotFoundException("Tag not found!");
		}
		List<TagMovieRelation> relations = TagMovieRelation.find("tag", tag).list();
		return relations.stream().map(TagMovieRelation::getMovie).toList();
	}
}
