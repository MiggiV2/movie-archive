package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.TagEntity;
import de.mymiggi.movie.api.entity.db.TagMovieRelation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class AddTagsAction
{
	private static final Logger LOG = Logger.getLogger(AddTagsAction.class.getSimpleName());

	public void run(Long movieId, String[] tags)
	{
		LOG.infof("Adding tags to movie %s: ", movieId);
		MovieEntity movieEntity = MovieEntity.findById(movieId);
		if (movieEntity == null)
		{
			LOG.warn("Movie not found!");
			throw new NotFoundException("Movie not found!");
		}
		for (String tag : tags)
		{
			addTag(tag, movieEntity);
		}
		LOG.info("Done");
	}

	private void addTag(String tag, MovieEntity movieEntity)
	{
		tag = tag.toLowerCase();
		if (tag.startsWith("• "))
		{
			tag = tag.replace("• ", "");
		}
		if (tag.startsWith("* "))
		{
			tag = tag.replace("* ", "");
		}
		if (tag.startsWith("\"") && tag.endsWith("\""))
		{
			tag = tag.replace("\"", "");
		}
		if (tag.startsWith("'") && tag.endsWith("'"))
		{
			tag = tag.replace("'", "");
		}
		tag = tag.trim();
		if (tag.isBlank())
		{
			return;
		}

		Optional<TagEntity> tagOpt = TagEntity.find("name", tag).firstResultOptional();
		TagEntity tagEntity = tagOpt.orElse(new TagEntity(tag, LocalDateTime.now()));
		if (!tagEntity.isPersistent())
		{
			LOG.infof("Saved new Tag: %s", tag);
			tagEntity.persist();
		}

		TagMovieRelation relation = new TagMovieRelation(movieEntity, tagEntity);
		relation.persist();
	}
}
