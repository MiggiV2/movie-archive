package de.mymiggi.movie.api.actions.user.mobile;

import de.mymiggi.movie.api.entity.oauth.KeycloakTokens;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import de.mymiggi.movie.api.service.ExchangeService;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

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

	public KeycloakTokens run(TokenRequest credentials)
	{
		try
		{
			return getTokens(credentials);
		}
		catch (WebApplicationException webEx)
		{
			if (webEx.getResponse().getStatus() == 401)
			{
				throw new UnauthorizedException("Incorrect username or password");
			}
			LOG.error("Unexpected response code in login request!", webEx);
			throw new InternalServerErrorException("Unexpected response code in login request!");
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
