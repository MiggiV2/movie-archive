package de.mymiggi.movie.auth;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.mymiggi.movie.auth.action.LoginAction;
import de.mymiggi.movie.auth.action.RefreshAction;
import de.mymiggi.movie.auth.entity.config.OAuthConfig;
import de.mymiggi.movie.auth.entity.login.UserCredentials;

@Path("movie-archive/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource
{
	LoginAction loginAction;

	RefreshAction refreshAction;

	OAuthConfig oAuthConfig;

	@Inject
	public AuthResource(LoginAction loginAction, RefreshAction refreshAction, OAuthConfig oAuthConfig)
	{
		this.loginAction = loginAction;
		this.refreshAction = refreshAction;
		this.oAuthConfig = oAuthConfig;
	}

	@POST
	@Path("login")
	public Response login(UserCredentials credentials)
	{
		return loginAction.run(credentials, oAuthConfig);
	}

	@POST
	@Path("token-refresh")
	public Response refresh(UserCredentials userCredentials)
	{
		return refreshAction.run(userCredentials, oAuthConfig);
	}
}