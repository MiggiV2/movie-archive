package de.mymiggi.movie.api.service;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.mymiggi.movie.api.entity.oauth.KeycloakTokens;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;

@Path("/protocol/openid-connect")
@RegisterRestClient
@RegisterProvider(value = ExchangeMapper.class, priority = 50)
public interface ExchangeService
{

	@POST
	@Path("token")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	KeycloakTokens getTokens(@BeanParam TokenRequest tokenRequest) throws ExchangeException;
}