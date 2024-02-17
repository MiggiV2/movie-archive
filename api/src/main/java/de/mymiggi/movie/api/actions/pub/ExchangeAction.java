package de.mymiggi.movie.api.actions.pub;

import de.mymiggi.movie.api.entity.config.OAuthConfig;
import de.mymiggi.movie.api.entity.oauth.KeycloakTokens;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import de.mymiggi.movie.api.service.ExchangeService;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ExchangeAction
{
	@Inject
	OAuthConfig redirectURL;
	@Inject
	@RestClient
	ExchangeService exchangeService;

	public KeycloakTokens runCode(TokenRequest tokenRequest)
	{
		tokenRequest.setGrandType("authorization_code");
		if (tokenRequest.getCode() == null || tokenRequest.getCode().isBlank())
		{
			throw new BadRequestException("You need your code for exchange");
		}
		return run(tokenRequest, exchangeService, redirectURL);
	}

	public KeycloakTokens runRefresh(TokenRequest tokenRequest)
	{
		tokenRequest.setGrandType("refresh_token");
		if (tokenRequest.getRefreshToken() == null || tokenRequest.getRefreshToken().isBlank())
		{
			throw new BadRequestException("You need your refreshToken for exchange");
		}
		return run(tokenRequest, exchangeService, redirectURL);
	}

	private KeycloakTokens run(TokenRequest tokenRequest, ExchangeService exchangeService, OAuthConfig redirectURL)
	{
		SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
		tokenRequest.setClientID(config.getConfigValue("quarkus.oidc.client-id").getValue());
		tokenRequest.setClientSecret(config.getConfigValue("quarkus.oidc.credentials.secret").getValue());
		tokenRequest.setRedircetURL(redirectURL.RedirectURL());
		try
		{
			return exchangeService.useToken(tokenRequest);
		}
		catch (WebApplicationException e)
		{
			throw new BadRequestException(e.getMessage());
		}
	}
}
