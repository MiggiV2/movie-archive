package de.mymiggi.movie.api;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(AuditResource.class)
@TestSecurity(user = "admin", roles = "movie_admins@sso.mymiggi.de")
public class AuditResourceTest
{
	@Test
	void testAuditLogPageCount()
	{
		given()
			.when()
			.get("pages")
			.then()
			.statusCode(200);
	}

	@Test
	void testAuditLogPage()
	{
		given()
			.when()
			.queryParam("page", "0")
			.get()
			.then()
			.statusCode(200);
	}
}
