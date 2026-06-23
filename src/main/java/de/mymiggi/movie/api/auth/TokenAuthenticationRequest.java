package de.mymiggi.movie.api.auth;

import io.quarkus.security.identity.request.BaseAuthenticationRequest;

/**
 * Carries a presented opaque Bearer secret from the {@link TokenAuthenticationMechanism}
 * to the {@link TokenIdentityProvider}.
 */
public class TokenAuthenticationRequest extends BaseAuthenticationRequest
{
	private final String secret;

	public TokenAuthenticationRequest(String secret)
	{
		this.secret = secret;
	}

	public String getSecret()
	{
		return secret;
	}
}
