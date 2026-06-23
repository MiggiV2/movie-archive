package de.mymiggi.movie.api.actions.token;

import de.mymiggi.movie.api.actions.auditlog.SaveAuditLogAction;
import de.mymiggi.movie.api.auth.TokenSecrets;
import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.TokenRole;
import de.mymiggi.movie.api.entity.db.ApiTokenEntity;
import de.mymiggi.movie.api.entity.token.CreateTokenRequest;
import de.mymiggi.movie.api.entity.token.TokenSecretView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class CreateTokenAction
{
	public TokenSecretView run(String principal, TokenRole role, CreateTokenRequest request)
	{
		if (request == null || request.name() == null || request.name().isBlank())
		{
			throw new BadRequestException("Token name is required");
		}
		String secret = TokenSecrets.generateSecret();
		ApiTokenEntity entity = new ApiTokenEntity(request.name().trim(), TokenSecrets.hash(secret), principal, role);
		entity.persist();
		new SaveAuditLogAction().run(principal, AuditLogType.TOKEN_CREATE, String.format("Created API token '%s'", entity.name));
		return new TokenSecretView(entity.id, entity.name, entity.role, entity.createdAt, secret);
	}
}
