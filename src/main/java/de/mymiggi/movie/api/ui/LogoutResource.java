package de.mymiggi.movie.api.ui;

import io.quarkus.oidc.OidcSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

/**
 * Local logout for the Qute UI: clears the OIDC session cookie and returns to the home
 * page. We do a local logout (rather than RP-initiated) because the OpenID Provider does
 * not advertise an {@code end_session_endpoint}.
 */
@Path("ui/logout")
public class LogoutResource
{
	private static final URI HOME = URI.create("/ui");

	@Inject
	OidcSession oidcSession;

	@GET
	public Uni<Response> logout()
	{
		return oidcSession.logout()
			.onItemOrFailure().transform((v, t) -> Response.seeOther(HOME).build());
	}
}
