package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.user.mobile.LoginAction;
import de.mymiggi.movie.api.actions.user.mobile.RefreshAction;
import de.mymiggi.movie.api.entity.oauth.KeycloakTokens;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
	public KeycloakTokens login(TokenRequest credentials)
	{
		return loginAction.run(credentials);
	}

	@POST
	@Path("token-refresh")
	public KeycloakTokens refresh(TokenRequest userCredentials)
	{
		return refreshAction.run(userCredentials);
	}
}