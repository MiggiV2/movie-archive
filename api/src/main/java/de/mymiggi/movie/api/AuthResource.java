package de.mymiggi.movie.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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