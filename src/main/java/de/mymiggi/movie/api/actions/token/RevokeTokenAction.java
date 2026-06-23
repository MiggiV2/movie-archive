package de.mymiggi.movie.api.actions.token;

import de.mymiggi.movie.api.actions.auditlog.SaveAuditLogAction;
import de.mymiggi.movie.api.entity.AuditLogType;
import de.mymiggi.movie.api.entity.db.ApiTokenEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class RevokeTokenAction
{
	public void run(String principal, long id)
	{
		ApiTokenEntity entity = ApiTokenEntity.findById(id);
		if (entity == null || !principal.equals(entity.principal))
		{
			// Do not reveal existence of another user's token.
			throw new NotFoundException("Token not found");
		}
		String name = entity.name;
		entity.delete();
		new SaveAuditLogAction().run(principal, AuditLogType.TOKEN_REVOKE, String.format("Revoked API token '%s'", name));
	}
}
