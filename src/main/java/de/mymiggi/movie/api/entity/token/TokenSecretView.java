package de.mymiggi.movie.api.entity.token;

import de.mymiggi.movie.api.entity.TokenRole;

import java.time.LocalDateTime;

/**
 * Returned exactly once when a token is created. Carries the plaintext secret,
 * which can never be retrieved again.
 */
public record TokenSecretView(Long id, String name, TokenRole role, LocalDateTime createdAt, String secret)
{
}
