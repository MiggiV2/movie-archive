package de.mymiggi.movie.api.actions.admin;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
import de.mymiggi.movie.api.actions.auditlog.SaveAuditLogAction;
import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import de.mymiggi.movie.api.entity.metadata.Title;
import de.mymiggi.movie.api.service.MetaDataService;
import io.quarkus.oidc.UserInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class AddMovieAction extends AbstractAuditLogAction
{
	private static final Logger log = LoggerFactory.getLogger(AddMovieAction.class);
	@Inject
	UserInfo userInfo;

	@Inject
	MetaDataService metaDataService;

	public DetailedMovie run(DetailedMovie detailedMovie)
	{
		if (detailedMovie == null)
		{
			throw new BadRequestException("You need an object!");
		}
		MovieEntity movieEntity = toMovieEntity(detailedMovie);
		if (!checkMovie(movieEntity))
		{
			throw new BadRequestException("You object needs: String name, String type, int year!");
		}
		String firstLetter = String.valueOf(movieEntity.name.charAt(0));
		movieEntity.uuid = firstLetter + MovieEntity.startWith(firstLetter).size();
		movieEntity.persist();
		Optional<MovieMetaData> response = fetchMetaData(detailedMovie, movieEntity);
		if (response.isPresent())
		{
			MovieMetaData movieMetaData = response.get();
			movieMetaData.setMovieEntity(movieEntity);
			movieMetaData.persist();
			detailedMovie = new DetailedMovie(movieEntity, movieMetaData);
		}
		new SaveAuditLogAction().run(userInfo, this, String.format("Added movie '%s'", movieEntity.name), movieEntity);
		return detailedMovie;
	}

	private boolean checkMovie(MovieEntity movieEntity)
	{
		return movieEntity.name != null && movieEntity.type != null && movieEntity.year != 0;
	}

	private MovieEntity toMovieEntity(DetailedMovie detailedMovie)
	{
		return new MovieEntity(detailedMovie.getYear(), detailedMovie.getTitle(), detailedMovie.getBlock(), detailedMovie.getWikiUrl(), detailedMovie.getType());
	}

	private Optional<MovieMetaData> fetchMetaData(DetailedMovie detailedMovie, MovieEntity movieEntity)
	{
		// Get external ID if not provided
		if (detailedMovie.getExternalId() == null || detailedMovie.getExternalId().isBlank())
		{
			Optional<MovieMetaData> metaData = metaDataService.getMetaData(movieEntity);
			metaData.ifPresent(movieMetaData -> detailedMovie.setExternalId(movieMetaData.getImdbId()));
		}
		// Fetch FULL metadata by ID
		log.info("Fetched meta data for movie '{}'", detailedMovie.getExternalId());
		Optional<Title> title = metaDataService.getMetaDataById(detailedMovie.getExternalId());
		return title.map(MovieMetaData::new);
	}

	@Override
	public AuditLogType getLogType()
	{
		return AuditLogType.ADD;
	}
}
