package de.mymiggi.movie.api.ui;

import de.mymiggi.movie.api.actions.token.CreateTokenAction;
import de.mymiggi.movie.api.actions.token.ListTokensAction;
import de.mymiggi.movie.api.actions.token.RevokeTokenAction;
import de.mymiggi.movie.api.entity.TokenRole;
import de.mymiggi.movie.api.entity.token.CreateTokenRequest;
import de.mymiggi.movie.api.entity.token.TokenSecretView;
import de.mymiggi.movie.api.entity.token.TokenView;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

/**
 * Self-service API token management for any authenticated user. Owns its tokens via the
 * {@link UiSession} principal; the plaintext secret is rendered once right after creation.
 */
@Path("ui/tokens")
public class AccountTokenResource
{
	@Inject
	UiSession session;
	@Inject
	CreateTokenAction createTokenAction;
	@Inject
	ListTokensAction listTokensAction;
	@Inject
	RevokeTokenAction revokeTokenAction;

	@CheckedTemplate
	public static class Templates
	{
		public static native TemplateInstance tokens(List<TokenView> tokens, TokenSecretView created, boolean isAdmin, String username);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance list()
	{
		return Templates.tokens(listTokensAction.run(principal()), null, session.isAdmin(), session.getUsername());
	}

	@POST
	@Transactional
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance create(@RestForm String name)
	{
		TokenSecretView created = createTokenAction.run(principal(), currentRole(), new CreateTokenRequest(name));
		return Templates.tokens(listTokensAction.run(principal()), created, session.isAdmin(), session.getUsername());
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public Response revoke(@PathParam("id") long id)
	{
		revokeTokenAction.run(principal(), id);
		return Response.ok().header("HX-Redirect", "/ui/tokens").build();
	}

	private String principal()
	{
		return session.getUsername();
	}

	private TokenRole currentRole()
	{
		return session.isAdmin() ? TokenRole.ADMIN : TokenRole.USER;
	}
}
