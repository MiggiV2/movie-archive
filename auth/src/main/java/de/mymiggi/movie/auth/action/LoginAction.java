package de.mymiggi.movie.auth.action;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.mymiggi.movie.auth.entity.KeycloakTokens;
import de.mymiggi.movie.auth.entity.MsgType;
import de.mymiggi.movie.auth.entity.ResponseMsg;
import de.mymiggi.movie.auth.entity.config.OAuthConfig;
import de.mymiggi.movie.auth.entity.login.LoginRequest;
import de.mymiggi.movie.auth.entity.login.UserCredentials;
import de.mymiggi.movie.auth.service.LoginService;

@ApplicationScoped
public class LoginAction
{
	private static final Logger LOG = Logger.getLogger(LoginAction.class.getSimpleName());

	@Inject
	@RestClient
	LoginService loginService;

	public Response run(UserCredentials credentials, OAuthConfig oAuthConfig)
	{
		try
		{
			return Response.ok(getTokens(credentials, oAuthConfig)).build();
		}
		catch (WebApplicationException webEx)
		{
			if (webEx.getResponse().getStatus() == 401)
			{
				ResponseMsg msg = new ResponseMsg("Incorrect username or password", MsgType.ERROR);
				return Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();
			}
			LOG.error("Unexpected response code in login request!", webEx);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	public KeycloakTokens getTokens(UserCredentials credentials, OAuthConfig oAuthConfig) throws WebApplicationException
	{

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setGrandType("password");
		loginRequest.setClientID(oAuthConfig.ClientID());
		loginRequest.setClientSecret(oAuthConfig.ClientSecret());
		loginRequest.setUsername(credentials.getUsername());
		loginRequest.setPassword(credentials.getPassword());
		return loginService.getTokens(loginRequest);
	}
}
