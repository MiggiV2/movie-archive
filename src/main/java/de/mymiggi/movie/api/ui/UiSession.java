package de.mymiggi.movie.api.ui;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Per-request helper exposing auth state to the Qute UI resources.
 * In dev/test mode (where HTTP permissions are {@code permit} and there is no
 * OIDC session) the user is treated as an admin so the full UI can be exercised.
 */
@RequestScoped
public class UiSession
{
	@Inject
	SecurityIdentity identity;

	@ConfigProperty(name = "de.mymiggi.admin-role", defaultValue = "movie_admins@sso.mymiggi.de")
	String adminRole;

	public boolean isAdmin()
	{
		if (LaunchMode.current().isDevOrTest())
		{
			return true;
		}
		return !identity.isAnonymous() && identity.hasRole(adminRole);
	}

	public String getUsername()
	{
		if (identity.isAnonymous())
		{
			return LaunchMode.current().isDevOrTest() ? "Dev" : "Guest";
		}
		return identity.getPrincipal().getName();
	}
}
