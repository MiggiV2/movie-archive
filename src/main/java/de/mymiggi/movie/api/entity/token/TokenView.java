package de.mymiggi.movie.api.entity.token;

import de.mymiggi.movie.api.entity.TokenRole;
import de.mymiggi.movie.api.entity.db.ApiTokenEntity;

import java.time.LocalDateTime;

/**
 * Token metadata returned to the owner. Never carries the plaintext secret or its hash.
 */
public record TokenView(Long id, String name, TokenRole role, LocalDateTime createdAt, LocalDateTime lastUsedAt)
{
	public static TokenView from(ApiTokenEntity entity)
	{
		return new TokenView(entity.id, entity.name, entity.role, entity.createdAt, entity.lastUsedAt);
	}
}
