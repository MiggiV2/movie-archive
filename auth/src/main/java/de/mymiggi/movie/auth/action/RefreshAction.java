package de.mymiggi.movie.auth.action;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.mymiggi.movie.auth.entity.MsgType;
import de.mymiggi.movie.auth.entity.ResponseMsg;
import de.mymiggi.movie.auth.entity.config.OAuthConfig;
import de.mymiggi.movie.auth.entity.login.LoginRequest;
import de.mymiggi.movie.auth.entity.login.UserCredentials;
import de.mymiggi.movie.auth.service.LoginService;

@ApplicationScoped
public class RefreshAction
{
	private static final Logger LOG = Logger.getLogger(RefreshAction.class.getSimpleName());
	@Inject
	@RestClient
	LoginService loginService;

	private static LoginRequest createLoginRequest(UserCredentials userCredentials, OAuthConfig config)
	{
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setRefreshToken(userCredentials.getRefreshToken());
		loginRequest.setGrandType("refresh_token");
		loginRequest.setClientID(config.ClientID());
		loginRequest.setClientSecret(config.ClientSecret());
		return loginRequest;
	}

	public Response run(UserCredentials userCredentials, OAuthConfig config)
	{
		if (userCredentials.getRefreshToken() == null || userCredentials.getRefreshToken().isBlank())
		{
			ResponseMsg msg = new ResponseMsg("You need \"refreshToken\": \"YOUR_TOKEN\" in your json body!", MsgType.ERROR);
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
		}
		LoginRequest loginRequest = createLoginRequest(userCredentials, config);
		try
		{
			return Response.ok(loginService.getTokens(loginRequest)).build();
		}
		catch (WebApplicationException webEx)
		{
			if (webEx.getResponse().getStatus() == 400)
			{
				ResponseMsg msg = new ResponseMsg("Invalid refresh token", MsgType.ERROR);
				return Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();
			}
			LOG.error("Unexpected response code in refresh request!", webEx);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
