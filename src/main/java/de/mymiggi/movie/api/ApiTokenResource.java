package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.token.CreateTokenAction;
import de.mymiggi.movie.api.actions.token.ListTokensAction;
import de.mymiggi.movie.api.actions.token.RevokeTokenAction;
import de.mymiggi.movie.api.entity.TokenRole;
import de.mymiggi.movie.api.entity.token.CreateTokenRequest;
import de.mymiggi.movie.api.entity.token.TokenSecretView;
import de.mymiggi.movie.api.entity.token.TokenView;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Path("api/v2/token")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiTokenResource
{
	@Inject
	SecurityIdentity identity;
	@Inject
	CreateTokenAction createTokenAction;
	@Inject
	ListTokensAction listTokensAction;
	@Inject
	RevokeTokenAction revokeTokenAction;

	@ConfigProperty(name = "de.mymiggi.admin-role", defaultValue = "movie_admins@sso.mymiggi.de")
	String adminRole;

	@POST
	@Transactional
	public TokenSecretView create(CreateTokenRequest request)
	{
		return createTokenAction.run(principal(), currentRole(), request);
	}

	@GET
	public List<TokenView> list()
	{
		return listTokensAction.run(principal());
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public void revoke(@PathParam("id") long id)
	{
		revokeTokenAction.run(principal(), id);
	}

	private String principal()
	{
		return identity.getPrincipal().getName();
	}

	private TokenRole currentRole()
	{
		return identity.hasRole(adminRole) ? TokenRole.ADMIN : TokenRole.USER;
	}
}
