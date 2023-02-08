package de.mymiggi.movie.api.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.DatatypeConverter;

import org.jboss.logging.Logger;

import de.mymiggi.movie.api.entity.db.MovieEntity;

@ApplicationScoped
public class SyncService
{
	private static final Logger LOG = Logger.getLogger(SyncService.class.getSimpleName());
	private static final Map<Long, String> CACHE = new HashMap<>();
	private long lastReset = 0;
	private MessageDigest md;

	SyncService()
	{
		try
		{
			md = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e)
		{
			LOG.error("MD5 not found for " + MessageDigest.class, e);
			LOG.warn(SyncService.class + " is not working!");
		}
	}

	/**
	 * @return A HashMap containing the movie id and md5 hash
	 */
	public Map<Long, String> getHashMap()
	{
		if (System.currentTimeMillis() - lastReset < 1000 * 60)
		{
			return CACHE;
		}

		CACHE.clear();
		lastReset = System.currentTimeMillis();

		List<MovieEntity> movies = MovieEntity.listAll();
		for (MovieEntity movie : movies)
		{
			CACHE.put(movie.id, getHash(movie));
		}

		return CACHE;
	}

	private String getHash(MovieEntity entity)
	{
		if (md == null)
		{
			LOG.warn(SyncService.class + " is not working!");
			return "";
		}
		md.update(entity.toString().getBytes());
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest).toUpperCase();
	}
}
