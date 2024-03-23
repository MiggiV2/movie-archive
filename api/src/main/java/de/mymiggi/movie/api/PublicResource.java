package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.pub.ExchangeAction;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.oauth.KeycloakTokens;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("movie-archive/public")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicResource
{
	DefaultPage defaultPage;
	ExchangeAction exchangeAction;

	@Inject
	public PublicResource(DefaultPage defaultPage, ExchangeAction exchangeAction)
	{
		this.defaultPage = defaultPage;
		this.exchangeAction = exchangeAction;
	}

	@GET
	@Path("movie-count")
	@Produces(MediaType.TEXT_PLAIN)
	public long getMovieCount()
	{
		return MovieEntity.count();
	}

	@GET
	@Path("movie-page-count")
	@Produces(MediaType.TEXT_PLAIN)
	public long getMoviePageCount()
	{
		return Math.ceilDiv(MovieEntity.count(), defaultPage.Size()) - 1;
	}

	@POST
	@Path("code-exchange")
	public KeycloakTokens exchangeCode(TokenRequest tokenRequest)
	{
		return exchangeAction.runCode(tokenRequest);
	}

	@POST
	@Path("refresh-exchange")
	public KeycloakTokens exchangeRefresh(TokenRequest tokenRequest)
	{
		return exchangeAction.runRefresh(tokenRequest);
	}
}
