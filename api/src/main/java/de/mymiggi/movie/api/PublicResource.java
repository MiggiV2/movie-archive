package de.mymiggi.movie.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.mymiggi.movie.api.actions.pub.ExchangeAction;
import de.mymiggi.movie.api.actions.pub.GetMovieCountAction;
import de.mymiggi.movie.api.actions.pub.GetMoviePageCountAction;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.config.OAuthRedirectURL;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import de.mymiggi.movie.api.service.ExchangeService;

@Path("movie-archive/public")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PublicResource
{
	@Inject
	DefaultPage defaultPage;

	@Inject
	OAuthRedirectURL redirectURL;

	@Inject
	@RestClient
	ExchangeService exchangeService;

	@GET
	@Path("movie-count")
	public Response getMovieCount()
	{
		return new GetMovieCountAction().run();
	}

	@GET
	@Path("movie-page-count")
	public Response getMoviePageCount()
	{
		return new GetMoviePageCountAction().run(defaultPage);
	}

	@POST
	@Path("code-exchange")
	public Response exchangeCode(TokenRequest tokenRequest)
	{
		return new ExchangeAction().runCode(tokenRequest, exchangeService, redirectURL);
	}

	@POST
	@Path("refresh-exchange")
	public Response exchangeRefresh(TokenRequest tokenRequest)
	{
		return new ExchangeAction().runRefresh(tokenRequest, exchangeService, redirectURL);
	}
}
