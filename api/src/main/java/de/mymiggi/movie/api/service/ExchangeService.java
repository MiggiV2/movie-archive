package de.mymiggi.movie.api.service;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	KeycloakTokens useToken(@BeanParam TokenRequest tokenRequest);
}