package de.mymiggi.movie.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.Test;

import io.quarkus.vertx.http.runtime.security.ChallengeData;

/**
 * Unit-level guard for {@link TokenAuthenticationMechanism#getChallenge}. The mechanism
 * outranks OIDC, so it must only raise a 401 challenge when it actually owns the request
 * (an {@code Authorization: Bearer mvk_*} token). For everything else it must defer (null)
 * so OIDC issues its interactive redirect instead of a flat 401.
 */
class TokenAuthenticationMechanismTest
{
	private final TokenAuthenticationMechanism mechanism = new TokenAuthenticationMechanism();

	private RoutingContext contextWithAuthHeader(String headerValue)
	{
		HttpServerRequest request = mock(HttpServerRequest.class);
		when(request.getHeader("Authorization")).thenReturn(headerValue);
		RoutingContext context = mock(RoutingContext.class);
		when(context.request()).thenReturn(request);
		return context;
	}

	@Test
	void noAuthHeaderDefersChallengeToOidc()
	{
		ChallengeData challenge = mechanism.getChallenge(contextWithAuthHeader(null))
			.await().indefinitely();
		assertNull(challenge, "Without our token the mechanism must defer so OIDC can redirect");
	}

	@Test
	void nonMvkBearerDefersChallengeToOidc()
	{
		ChallengeData challenge = mechanism.getChallenge(contextWithAuthHeader("Bearer some.jwt.token"))
			.await().indefinitely();
		assertNull(challenge, "Non-mvk bearer tokens belong to OIDC/JWT, not this mechanism");
	}

	@Test
	void mvkBearerRaises401Challenge()
	{
		ChallengeData challenge = mechanism.getChallenge(contextWithAuthHeader("Bearer mvk_whatever"))
			.await().indefinitely();
		assertEquals(401, challenge.status, "An mvk_ token this mechanism owns must yield a 401 challenge");
	}
}
