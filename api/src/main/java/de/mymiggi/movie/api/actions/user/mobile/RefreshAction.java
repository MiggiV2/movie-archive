package de.mymiggi.movie.api.actions.user.mobile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import de.mymiggi.movie.api.service.ExchangeService;

@ApplicationScoped
public class RefreshAction
{
	private static final Logger LOG = Logger.getLogger(RefreshAction.class.getSimpleName());
	@Inject
	@RestClient
	ExchangeService loginService;

	@ConfigProperty(name = "quarkus.oidc.client-id")
	String clientID;

	@ConfigProperty(name = "quarkus.oidc.credentials.secret")
	String clientSecret;

	public Response run(TokenRequest userCredentials)
	{
		if (userCredentials.getRefreshToken() == null || userCredentials.getRefreshToken().isBlank())
		{
			ShortMessage msg = new ShortMessage("You need \"refreshToken\": \"YOUR_TOKEN\" in your json body!", MessageStatus.ERROR);
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
		}
		TokenRequest loginRequest = createLoginRequest(userCredentials);
		try
		{
			return Response.ok(loginService.useToken(loginRequest)).build();
		}
		catch (WebApplicationException webEx)
		{
			if (webEx.getResponse().getStatus() == 400)
			{
				ShortMessage msg = new ShortMessage("Invalid refresh token", MessageStatus.ERROR);
				return Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();
			}
			LOG.error("Unexpected response code in refresh request!", webEx);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	private TokenRequest createLoginRequest(TokenRequest userCredentials)
	{
		TokenRequest loginRequest = new TokenRequest();
		loginRequest.setRefreshToken(userCredentials.getRefreshToken());
		loginRequest.setGrandType("refresh_token");
		loginRequest.setClientID(clientID);
		loginRequest.setClientSecret(clientSecret);
		return loginRequest;
	}
}
