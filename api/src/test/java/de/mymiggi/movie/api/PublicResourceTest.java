package de.mymiggi.movie.api;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
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
			.body(is("20"));
	}

	@Test
	void testMoviePageCount()
	{
		given().when()
			.get("movie-page-count")
			.then()
			.statusCode(200)
			.body(is("3"));
	}

	@Test
	void testConfig()
	{
		given().when()
			.get("config")
			.then()
			.statusCode(200)
			.body("authServerUrl", is("https://sso.mymiggi.de/oauth2/openid/movies-spa"))
			.body("authClientId", is("movies-spa"))
			.body("adminRole", is("movie_admins@sso.mymiggi.de"))
			.body("platformOwner", is("Dev"));
	}
}