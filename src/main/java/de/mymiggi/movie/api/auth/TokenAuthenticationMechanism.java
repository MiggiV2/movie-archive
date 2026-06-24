package de.mymiggi.movie.api.auth;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.Set;

/**
 * Authenticates requests presenting an opaque {@code Authorization: Bearer mvk_<secret>}
 * token. Only engages for secrets carrying the {@code mvk_} prefix; for any other request
 * it returns a null identity so the OIDC interactive flow (and JWT bearer handling) are
 * left untouched. The blocking hash + DB lookup happens in {@link TokenIdentityProvider},
 * never on the I/O thread here.
 * <p>
 * Mechanisms are ordered by {@link #getPriority()} (descending), not by the CDI
 * {@code @Priority} annotation. We sit just above the default 1000 so we get first
 * look at the {@code Authorization} header and short-circuit OIDC's bearer
 * introspection for our opaque {@code mvk_} tokens.
 */
@ApplicationScoped
public class TokenAuthenticationMechanism implements HttpAuthenticationMechanism
{
	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	public int getPriority()
	{
		// OIDC's mechanism defaults to DEFAULT_PRIORITY + 1 (1001); sit clearly above it
		// so our opaque-token check runs before OIDC attempts to introspect the bearer.
		return HttpAuthenticationMechanism.DEFAULT_PRIORITY + 100;
	}

	@Override
	public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager)
	{
		String secret = extractSecret(context);
		if (secret == null)
		{
			return Uni.createFrom().nullItem();
		}
		return identityProviderManager.authenticate(new TokenAuthenticationRequest(secret));
	}

	@Override
	public Uni<ChallengeData> getChallenge(RoutingContext context)
	{
		String secret = extractSecret(context);
		if (secret == null)
		{
			// Not our request: defer so the next mechanism (OIDC) can issue its redirect
			// challenge instead of us short-circuiting the interactive login with a 401.
			return Uni.createFrom().nullItem();
		}
		return Uni.createFrom().item(new ChallengeData(401, null, null));
	}

	@Override
	public Set<Class<? extends AuthenticationRequest>> getCredentialTypes()
	{
		return Collections.singleton(TokenAuthenticationRequest.class);
	}

	private String extractSecret(RoutingContext context)
	{
		String header = context.request().getHeader("Authorization");
		if (header == null || !header.startsWith(BEARER_PREFIX))
		{
			return null;
		}
		String value = header.substring(BEARER_PREFIX.length()).trim();
		return value.startsWith(TokenSecrets.PREFIX) ? value : null;
	}
}
