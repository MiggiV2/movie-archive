package de.mymiggi;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import de.mymiggi.movie.api.service.ExchangeException;
import de.mymiggi.movie.api.service.ExchangeService;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ExchangeTest
{
	@Inject
	@RestClient
	ExchangeService exchangeService;

	@Test
	void test()
	{
		TokenRequest tokenRequest = new TokenRequest();
		tokenRequest.setClientID("");
		tokenRequest.setClientSecret("");
		tokenRequest.setCode("");
		tokenRequest.setGrandType("");
		tokenRequest.setRedircetURL("");
		try
		{
			exchangeService.getTokens(tokenRequest);
		}
		catch (ExchangeException e)
		{
			System.err.print(e);
		}
	}
}
