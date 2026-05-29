package de.mymiggi.movie.api.ui;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

@QuarkusTest
class RootResourceTest
{
	@Test
	void root_redirects_to_ui_without_auth()
	{
		given()
			.redirects().follow(false)
			.when().get("/")
			.then()
				.statusCode(303)
				.header("Location", endsWith("/ui"));
	}
}
