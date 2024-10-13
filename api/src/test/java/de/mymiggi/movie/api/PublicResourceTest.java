package de.mymiggi.movie.api;

import de.mymiggi.movie.api.entity.oauth.TokenRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(PublicResource.class)
@TestSecurity(user = "test", roles = "user")
public class PublicResourceTest
{
	@Test
	void testMovieCount()
	{
		given().when()
			.get("movie-count")
			.then()
			.statusCode(200)
			.body(is("363"));
	}

	@Test
	void testMoviePageCount()
	{
		given().when()
			.get("movie-page-count")
			.then()
			.statusCode(200)
			.body(is("12"));
	}

	@Test
	void testCodeExchangeFail()
	{
		given()
			.when()
			.contentType(ContentType.JSON)
			.body(buildTokenRequest())
			.post("code-exchange")
			.then()
			.statusCode(400)
			.body(is(""));
	}

	@Test
	void testRefreshExchangeFail()
	{
		given()
			.when()
			.contentType(ContentType.JSON)
			.body(buildTokenRequest())
			.post("refresh-exchange")
			.then()
			.statusCode(400)
			.body(is(""));
	}

	private TokenRequest buildTokenRequest()
	{
		TokenRequest tokenRequest = new TokenRequest();
		tokenRequest.setUsername("test");
		tokenRequest.setRefreshToken("test");
		tokenRequest.setGrandType("code");
		tokenRequest.setCode("test");
		return tokenRequest;
	}
}