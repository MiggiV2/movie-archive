package de.mymiggi.movie.api.actions.user.mobile;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.oauth.KeycloakTokens;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import de.mymiggi.movie.api.service.ExchangeService;

@ApplicationScoped
public class LoginAction
{
	private static final Logger LOG = Logger.getLogger(LoginAction.class.getSimpleName());

	@Inject
	@RestClient
	ExchangeService loginService;

	@ConfigProperty(name = "quarkus.oidc.client-id")
	String clientID;

	@ConfigProperty(name = "quarkus.oidc.credentials.secret")
	String clientSecret;

	public Response run(TokenRequest credentials)
	{
		try
		{
			return Response.ok(getTokens(credentials)).build();
		}
		catch (WebApplicationException webEx)
		{
			if (webEx.getResponse().getStatus() == 401)
			{
				ShortMessage msg = new ShortMessage("Incorrect username or password", MessageStatus.ERROR);
				return Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();
			}
			LOG.error("Unexpected response code in login request!", webEx);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	public KeycloakTokens getTokens(TokenRequest credentials) throws WebApplicationException
	{
		TokenRequest loginRequest = new TokenRequest();
		loginRequest.setGrandType("password");
		loginRequest.setClientID(clientID);
		loginRequest.setClientSecret(clientSecret);
		loginRequest.setUsername(credentials.getUsername());
		loginRequest.setPassword(credentials.getPassword());
		return loginService.useToken(loginRequest);
	}
}
