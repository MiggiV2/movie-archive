package de.mymiggi.movie.api;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(ServiceResource.class)
@TestSecurity(user = "admin", roles = "movie_admins@sso.mymiggi.de")
public class ServiceResourceTest
{
	@Test
	void testBlockUpdate()
	{
		given()
			.when()
			.contentType(ContentType.JSON)
			.post("trigger/block-update")
			.then()
			.statusCode(204);
	}

	@Test
	void testConfig()
	{
		given()
			.when()
			.get("config")
			.then()
			.statusCode(200);
	}
}
