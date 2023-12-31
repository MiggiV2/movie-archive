package de.mymiggi.movie.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import de.mymiggi.movie.api.actions.pub.ExchangeAction;
import de.mymiggi.movie.api.actions.pub.GetMoviePageCountAction;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.oauth.TokenRequest;

@Path("movie-archive/public")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicResource
{
	DefaultPage defaultPage;
	GetMoviePageCountAction getMoviePageCountAction;
	ExchangeAction exchangeAction;

	@Inject
	public PublicResource(DefaultPage defaultPage, GetMoviePageCountAction getMoviePageCountAction, ExchangeAction exchangeAction)
	{
		this.defaultPage = defaultPage;
		this.getMoviePageCountAction = getMoviePageCountAction;
		this.exchangeAction = exchangeAction;
	}

	@GET
	@Path("movie-count")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getMovieCount()
	{
		return Response.ok(MovieEntity.count()).build();
	}

	@GET
	@Path("movie-page-count")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getMoviePageCount()
	{
		return getMoviePageCountAction.run(defaultPage);
	}

	@POST
	@Path("code-exchange")
	public Response exchangeCode(TokenRequest tokenRequest)
	{
		return exchangeAction.runCode(tokenRequest);
	}

	@POST
	@Path("refresh-exchange")
	public Response exchangeRefresh(TokenRequest tokenRequest)
	{
		return exchangeAction.runRefresh(tokenRequest);
	}
}
