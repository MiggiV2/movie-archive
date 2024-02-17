package de.mymiggi.movie.api.actions.user.mobile;

import de.mymiggi.movie.api.entity.oauth.KeycloakTokens;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import de.mymiggi.movie.api.service.ExchangeService;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

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

	public KeycloakTokens run(TokenRequest userCredentials)
	{
		if (userCredentials.getRefreshToken() == null || userCredentials.getRefreshToken().isBlank())
		{
			throw new BadRequestException("You need \"refreshToken\": \"YOUR_TOKEN\" in your json body!");
		}
		TokenRequest loginRequest = createLoginRequest(userCredentials);
		try
		{
			return loginService.useToken(loginRequest);
		}
		catch (WebApplicationException webEx)
		{
			if (webEx.getResponse().getStatus() == 400)
			{
				throw new UnauthorizedException("Invalid refresh token");
			}
			LOG.error("Unexpected response code in refresh request!", webEx);
			throw new InternalServerErrorException("Unexpected response code in refresh request!");
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
