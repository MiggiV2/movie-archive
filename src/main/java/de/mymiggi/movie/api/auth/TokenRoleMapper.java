package de.mymiggi.movie.api.auth;

import de.mymiggi.movie.api.entity.TokenRole;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Maps the role snapshotted into a token to the application's OIDC role strings, so
 * the existing {@code quarkus.http.auth.permission} rules apply to tokens unchanged.
 */
@ApplicationScoped
public class TokenRoleMapper
{
	@ConfigProperty(name = "de.mymiggi.admin-role", defaultValue = "movie_admins@sso.mymiggi.de")
	String adminRole;

	@ConfigProperty(name = "de.mymiggi.user-role", defaultValue = "movie_group@sso.mymiggi.de")
	String userRole;

	public String toRoleString(TokenRole role)
	{
		return role == TokenRole.ADMIN ? adminRole : userRole;
	}
}
