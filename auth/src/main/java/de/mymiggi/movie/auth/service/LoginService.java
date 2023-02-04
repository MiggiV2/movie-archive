package de.mymiggi.movie.auth.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.mymiggi.movie.auth.entity.KeycloakTokens;
import de.mymiggi.movie.auth.entity.login.LoginRequest;

@Path("/protocol/openid-connect")
@RegisterRestClient
public interface LoginService
{
	@POST
	@Path("token")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	KeycloakTokens getTokens(@BeanParam LoginRequest tokenRequest);
}
