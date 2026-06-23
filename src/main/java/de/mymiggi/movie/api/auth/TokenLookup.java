package de.mymiggi.movie.api.auth;

import de.mymiggi.movie.api.entity.db.ApiTokenEntity;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

/**
 * Blocking token verification. Separate CDI bean so {@code @Transactional} (and the
 * request context) actually apply — a self-invoked transactional method on the provider
 * would be bypassed by the interceptor.
 */
@ApplicationScoped
public class TokenLookup
{
	@Inject
	TokenRoleMapper roleMapper;

	@ActivateRequestContext
	@Transactional
	public SecurityIdentity authenticate(String secret)
	{
		String hash = TokenSecrets.hash(secret);
		ApiTokenEntity token = ApiTokenEntity.find("tokenHash", hash).firstResult();
		if (token == null)
		{
			throw new AuthenticationFailedException("Unknown API token");
		}
		touchLastUsed(token);
		String principal = token.principal;
		return QuarkusSecurityIdentity.builder()
			.setPrincipal((Principal) () -> principal)
			.addRole(roleMapper.toRoleString(token.role))
			.build();
	}

	// Throttled: avoid a write on every request by only updating once per minute.
	private void touchLastUsed(ApiTokenEntity token)
	{
		LocalDateTime now = LocalDateTime.now();
		if (token.lastUsedAt == null || token.lastUsedAt.isBefore(now.minusMinutes(1)))
		{
			token.lastUsedAt = now;
			token.persist();
		}
	}
}
