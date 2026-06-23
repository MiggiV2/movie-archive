package de.mymiggi.movie.api.entity;

/**
 * Permission level snapshotted into an API token at creation time. Mapped to the
 * application's OIDC role strings by {@link de.mymiggi.movie.api.auth.TokenRoleMapper}.
 */
public enum TokenRole
{
	USER,
	ADMIN
}
