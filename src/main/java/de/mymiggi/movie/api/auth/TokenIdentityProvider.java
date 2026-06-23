package de.mymiggi.movie.api.auth;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Verifies an opaque Bearer secret against the stored token hashes. The lookup is
 * blocking (Panache / Hibernate ORM), so it runs via {@link AuthenticationRequestContext#runBlocking}
 * and is delegated to {@link TokenLookup}, whose {@code @Transactional} method opens the
 * ORM session and transaction needed for the read and the {@code lastUsedAt} update.
 */
@ApplicationScoped
public class TokenIdentityProvider implements IdentityProvider<TokenAuthenticationRequest>
{
	@Inject
	TokenLookup tokenLookup;

	@Override
	public Class<TokenAuthenticationRequest> getRequestType()
	{
		return TokenAuthenticationRequest.class;
	}

	@Override
	public Uni<SecurityIdentity> authenticate(TokenAuthenticationRequest request, AuthenticationRequestContext context)
	{
		return context.runBlocking(() -> tokenLookup.authenticate(request.getSecret()));
	}
}
