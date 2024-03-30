package de.mymiggi.movie.api.actions.user;

import de.mymiggi.movie.api.UserResource;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.TagEntity;
import de.mymiggi.movie.api.entity.db.TagMovieRelation;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class AddTagsAction
{
	private static final Logger LOG = Logger.getLogger(AddTagsAction.class.getSimpleName());

	@Inject
	UserResource userResource;

	public void run(Long movieId, String[] tags)
	{
		MovieEntity movieEntity = MovieEntity.findById(movieId);
		if (movieEntity == null)
		{
			LOG.warn("Movie not found!");
			throw new NotFoundException("Movie not found!");
		}
		List<TagMovieRelation> relations = TagMovieRelation.find("movie", movieEntity).list();
		for (String tag : tags)
		{
			if (!tag.isBlank())
			{
				addTag(tag, movieEntity, relations);
			}
		}
		removeOldRelations(Arrays.asList(tags), relations);
		LOG.info("Done");
	}

	private void removeOldRelations(List<String> tags, List<TagMovieRelation> oldRelations)
	{
		for (TagMovieRelation oldRelation : oldRelations)
		{
			String tagName = oldRelation.getTag().getName();
			boolean newTagsContainsOldTag = tags.contains(tagName);
			if (!newTagsContainsOldTag)
			{
				removeTag(oldRelation);
			}
		}
	}

	private void addTag(String tag, MovieEntity movieEntity, List<TagMovieRelation> relations)
	{
		Optional<TagEntity> tagOpt = TagEntity.find("name", tag).firstResultOptional();
		TagEntity tagEntity = tagOpt.orElse(new TagEntity(tag, LocalDateTime.now()));
		if (!tagEntity.isPersistent())
		{
			LOG.infof("Saved new Tag: %s", tag);
			tagEntity.persist();
		}

		boolean relationExists = relations.stream().anyMatch(r -> Objects.equals(r.getTag().id, tagEntity.id));

		if (!relationExists)
		{
			TagMovieRelation relation = new TagMovieRelation(movieEntity, tagEntity);
			relation.persist();
		}
	}

	private void removeTag(TagMovieRelation oldRelation)
	{
		// delete relation
		TagEntity oldTag = oldRelation.getTag();
		TagMovieRelation.deleteById(oldRelation.id);
		// delete unused tags
		long tagRelations = TagMovieRelation.find("tag", oldTag).count();
		if (tagRelations == 0)
		{
			Log.infof("Removed unused tag: %s", oldTag.getName());
			TagEntity.deleteById(oldTag.id);
		}
	}
}
