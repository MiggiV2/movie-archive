package de.mymiggi.movie.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.user.mobile.LoginAction;
import de.mymiggi.movie.api.actions.user.mobile.RefreshAction;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;

@Path("movie-archive/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource
{
	LoginAction loginAction;

	RefreshAction refreshAction;

	@Inject
	public AuthResource(LoginAction loginAction, RefreshAction refreshAction)
	{
		this.loginAction = loginAction;
		this.refreshAction = refreshAction;
	}

	@POST
	@Path("login")
	public Response login(TokenRequest credentials)
	{
		return loginAction.run(credentials);
	}

	@POST
	@Path("token-refresh")
	public Response refresh(TokenRequest userCredentials)
	{
		return refreshAction.run(userCredentials);
	}
}