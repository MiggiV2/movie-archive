package de.mymiggi.movie.api.actions.admin;

import de.mymiggi.movie.api.actions.auditlog.AbstractAuditLogAction;
import de.mymiggi.movie.api.actions.auditlog.SaveAuditLogAction;
import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.oidc.UserInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.Optional;

@ApplicationScoped
public class DeleteMovieAction extends AbstractAuditLogAction
{

	@Inject
	UserInfo userInfo;

	public void run(Long id)
	{
		if (id == null)
		{
			throw new NotFoundException("We need an id for your movie!");
		}
		Optional<MovieEntity> movieEntity = MovieEntity.findByIdOptional(id);
		if (movieEntity.isEmpty())
		{
			throw new NotFoundException("Can't find your movie!");
		}
		movieEntity.ifPresent(movie -> {
			handleMetadata(movie);
			movie.delete();
			new SaveAuditLogAction().run(userInfo, this, String.format("Removed movie '%s'", movie.name), movie);
		});
	}

	private void handleMetadata(MovieEntity movie)
	{
		Optional<MovieMetaData> metaData = MovieMetaData.find("movieEntity", movie).firstResultOptional();
		metaData.ifPresent(PanacheEntityBase::delete);
	}

	@Override
	public AuditLogType getLogType()
	{
		return AuditLogType.DELETE;
	}
}
