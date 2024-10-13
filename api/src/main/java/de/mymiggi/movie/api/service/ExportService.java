package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ExportService
{
	private static final List<String> SESSIONS = new LinkedList<>();
	private static final int LIST_SIZE_LIMIT = 100;
	private static final Logger LOG = LoggerFactory.getLogger(ExportService.class);

	public static String movieToCSV(MovieEntity movie)
	{
		return String.format("%d,\"%s\",%s,%s,\"%s\",%s,\"%s\"", movie.year, movie.name, movie.uuid,
			movie.block, movie.wikiUrl, movie.type, movie.originalName);
	}

	public String createOneTimeSession()
	{
		String session = UUID.randomUUID().toString();
		synchronized (SESSIONS)
		{
			if (SESSIONS.size() > LIST_SIZE_LIMIT)
			{
				SESSIONS.removeFirst();
			}
			SESSIONS.add(session);
		}
		return session;
	}

	public String getMovieCSV(String session)
	{
		synchronized (SESSIONS)
		{
			if (!SESSIONS.contains(session))
			{
				throw new UnauthorizedException("You are not allowed to access this export");
			}
			SESSIONS.remove(session);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("year,name,uuid,block,wikiUrl,type,originalName\n");
		for (PanacheEntityBase entity : getAllMovies())
		{
			MovieEntity movie = (MovieEntity)entity;
			sb.append(movieToCSV(movie)).append("\n");
		}
		return sb.toString();
	}

	// visible for testing
	protected List<PanacheEntityBase> getAllMovies()
	{
		return MovieEntity.listAll();
	}
}