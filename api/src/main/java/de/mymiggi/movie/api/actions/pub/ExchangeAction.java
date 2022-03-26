package de.mymiggi.movie.api.actions.pub;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.ConfigProvider;

import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.config.OAuthRedirectURL;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import de.mymiggi.movie.api.service.ExchangeException;
import de.mymiggi.movie.api.service.ExchangeService;
import io.smallrye.config.SmallRyeConfig;

public class ExchangeAction
{
	public Response runCode(TokenRequest tokenRequest, ExchangeService exchangeService, OAuthRedirectURL redirectURL)
	{
		tokenRequest.setGrandType("authorization_code");
		if (tokenRequest.getCode() == null || tokenRequest.getCode().isBlank())
		{
			ShortMessage message = new ShortMessage("You need your code for exchange", MessageStatus.ERROR);
			return Response.status(Status.BAD_REQUEST).entity(message).build();
		}
		return run(tokenRequest, exchangeService, redirectURL);
	}

	public Response runRefresh(TokenRequest tokenRequest, ExchangeService exchangeService, OAuthRedirectURL redirectURL)
	{
		tokenRequest.setGrandType("refresh_token");
		if (tokenRequest.getRefreshToken() == null || tokenRequest.getRefreshToken().isBlank())
		{
			ShortMessage message = new ShortMessage("You need your refreshToken for exchange", MessageStatus.ERROR);
			return Response.status(Status.BAD_REQUEST).entity(message).build();
		}
		return run(tokenRequest, exchangeService, redirectURL);
	}

	private Response run(TokenRequest tokenRequest, ExchangeService exchangeService, OAuthRedirectURL redirectURL)
	{
		SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
		tokenRequest.setClientID(config.getConfigValue("quarkus.oidc.client-id").getValue());
		tokenRequest.setClientSecret(config.getConfigValue("quarkus.oidc.credentials.secret").getValue());
		tokenRequest.setRedircetURL(redirectURL.RedirectURL());
		try
		{
			return Response.ok(exchangeService.getTokens(tokenRequest)).build();
		}
		catch (ExchangeException e)
		{
			return Response.status(400).entity(e.getMessage()).build();
		}
	}
}
