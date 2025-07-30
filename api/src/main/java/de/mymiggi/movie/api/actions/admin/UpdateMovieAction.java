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
import jakarta.ws.rs.NotFoundException;

import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class UpdateMovieAction extends AbstractAuditLogAction
{
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
		if (!checkMovie(detailedMovie))
		{
			throw new BadRequestException("You object needs: String name, String type, int year!");
		}
		Optional<MovieEntity> dbEntity = MovieEntity.findByIdOptional(detailedMovie.getId());
		if (dbEntity.isEmpty())
		{
			throw new NotFoundException("I can't find your movie by id " + detailedMovie.getId());
		}
		MovieEntity savedEntity = dbEntity.get();
		String message = getUpdateMessage(savedEntity, detailedMovie);
		update(savedEntity, detailedMovie);
		savedEntity.persist();
		MovieMetaData metaData = updateMetadata(detailedMovie, savedEntity);
		new SaveAuditLogAction().run(userInfo, this, message, savedEntity);
		return new DetailedMovie(savedEntity, metaData);
	}

	private MovieMetaData updateMetadata(DetailedMovie detailedMovie, MovieEntity movieEntity)
	{
		if (detailedMovie.getExternalId() == null || detailedMovie.getExternalId().isEmpty())
		{
			return new MovieMetaData();
		}
		Optional<MovieMetaData> meta = MovieMetaData.find("movieEntity", movieEntity).firstResultOptional();
		if (meta.isPresent())
		{
			MovieMetaData movieMetaData = meta.get();
			if (!movieMetaData.getImdbId().equals(detailedMovie.getExternalId()))
			{
				movieMetaData.delete();
				MovieMetaData.flush();
			}
		}
		return persistNewData(detailedMovie, movieEntity);
	}

	private MovieMetaData persistNewData(DetailedMovie detailedMovie, MovieEntity movieEntity)
	{
		Optional<Title> metaData = metaDataService.getMetaDataById(detailedMovie.getExternalId());
		if (metaData.isEmpty())
		{
			return new MovieMetaData();
		}
		MovieMetaData fetchData = new MovieMetaData(metaData.get());
		fetchData.setMovieEntity(movieEntity);
		fetchData.persist();
		return fetchData;
	}

	private String getUpdateMessage(MovieEntity entityOld, DetailedMovie entityNew)
	{
		if (!Objects.equals(entityOld.name, entityNew.getTitle()))
		{
			return String.format("Updated movie name from '%s' to '%s'", entityOld.name, entityNew.getTitle());
		}
		if (!Objects.equals(entityOld.wikiUrl, entityNew.getWikiUrl()))
		{
			return String.format("Updated movie wiki-url from '%s' to '%s'", entityOld.wikiUrl, entityNew.getWikiUrl());
		}
		if (!Objects.equals(entityOld.block, entityNew.getBlock()))
		{
			return String.format("Updated movie block from '%s' to '%s'", entityOld.block, entityNew.getBlock());
		}
		if (entityOld.year != entityNew.getYear())
		{
			return String.format("Updated movie year from '%s' to '%s'", entityOld.year, entityNew.getYear());
		}
		if (!Objects.equals(entityOld.originalName, entityNew.getOriginalName()))
		{
			return String.format("Updated movie originalName from '%s' to '%s'", entityOld.originalName, entityNew.getOriginalName());
		}
		return String.format("Updated movie '%s'", entityNew.getOriginalName());
	}

	private void update(MovieEntity entityOld, DetailedMovie entityNew)
	{
		entityOld.block = entityNew.getBlock();
		entityOld.name = entityNew.getTitle();
		entityOld.type = entityNew.getType();
		entityOld.wikiUrl = entityNew.getWikiUrl();
		entityOld.year = entityNew.getYear();
		entityOld.originalName = entityNew.getOriginalName();
	}

	private boolean checkMovie(DetailedMovie movieEntity)
	{
		return movieEntity.getTitle() != null && movieEntity.getType() != null && movieEntity.getYear() != 0 && movieEntity.getId() != 0;
	}

	@Override
	public AuditLogType getLogType()
	{
		return AuditLogType.UPDATE;
	}
}
