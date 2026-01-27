package de.mymiggi.movie.api;

import de.mymiggi.movie.api.entity.db.TagEntity;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(TagResource.class)
@TestSecurity(user = "test", roles = "movie_group@sso.mymiggi.de")
public class TagResourceTest
{
	@Test
	void testTagList()
	{
		given()
			.when()
			.get("tags")
			.then()
			.statusCode(200)
			.body("size()", is(479));
	}
}
