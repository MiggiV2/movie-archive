package de.mymiggi.movie.api;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the OIDC scope set that makes role parsing work in production.
 * <p>
 * Roles are sourced from the UserInfo endpoint ({@code quarkus.oidc.roles.source=userinfo}) and
 * read from the {@code groups} claim. Kanidm only emits that claim when the {@code groups} scope is
 * requested. Without it no roles are parsed, so {@code UiSession.isAdmin()} is always false and the
 * admin UI is hidden for genuine admins. The {@code @TestSecurity} tests inject roles directly and
 * never exercise this path, so they cannot catch a missing scope — this test does.
 */
@QuarkusTest
public class OidcScopesConfigTest
{
	@Test
	void groupsScopeIsRequestedSoRolesCanBeParsed()
	{
		String raw = ConfigProvider.getConfig()
			.getValue("quarkus.oidc.authentication.scopes", String.class);
		List<String> scopes = Arrays.stream(raw.split(","))
			.map(String::trim)
			.toList();

		assertTrue(scopes.contains("groups"),
			"OIDC must request the 'groups' scope so Kanidm emits the groups claim used for role parsing; configured scopes were: " + scopes);
	}
}
