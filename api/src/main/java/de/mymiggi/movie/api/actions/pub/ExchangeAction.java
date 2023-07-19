package de.mymiggi.movie.api.actions.pub;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.mymiggi.movie.api.entity.MessageStatus;
import de.mymiggi.movie.api.entity.ShortMessage;
import de.mymiggi.movie.api.entity.config.OAuthConfig;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import de.mymiggi.movie.api.service.ExchangeService;
import io.smallrye.config.SmallRyeConfig;

@ApplicationScoped
public class ExchangeAction
{
	@Inject
	OAuthConfig redirectURL;
	@Inject
	@RestClient
	ExchangeService exchangeService;

	public Response runCode(TokenRequest tokenRequest)
	{
		tokenRequest.setGrandType("authorization_code");
		if (tokenRequest.getCode() == null || tokenRequest.getCode().isBlank())
		{
			ShortMessage message = new ShortMessage("You need your code for exchange", MessageStatus.ERROR);
			return Response.status(Status.BAD_REQUEST).entity(message).build();
		}
		return run(tokenRequest, exchangeService, redirectURL);
	}

	public Response runRefresh(TokenRequest tokenRequest)
	{
		tokenRequest.setGrandType("refresh_token");
		if (tokenRequest.getRefreshToken() == null || tokenRequest.getRefreshToken().isBlank())
		{
			ShortMessage message = new ShortMessage("You need your refreshToken for exchange", MessageStatus.ERROR);
			return Response.status(Status.BAD_REQUEST).entity(message).build();
		}
		return run(tokenRequest, exchangeService, redirectURL);
	}

	private Response run(TokenRequest tokenRequest, ExchangeService exchangeService, OAuthConfig redirectURL)
	{
		SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
		tokenRequest.setClientID(config.getConfigValue("quarkus.oidc.client-id").getValue());
		tokenRequest.setClientSecret(config.getConfigValue("quarkus.oidc.credentials.secret").getValue());
		tokenRequest.setRedircetURL(redirectURL.RedirectURL());
		try
		{
			return Response.ok(exchangeService.useToken(tokenRequest)).build();
		}
		catch (WebApplicationException e)
		{
			return Response.status(400).entity(e.getMessage()).build();
		}
	}
}
