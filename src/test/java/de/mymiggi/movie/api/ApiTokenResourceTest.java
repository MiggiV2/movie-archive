package de.mymiggi.movie.api;

import de.mymiggi.movie.api.auth.TokenSecrets;
import de.mymiggi.movie.api.entity.TokenRole;
import de.mymiggi.movie.api.entity.db.ApiTokenEntity;
import de.mymiggi.movie.api.entity.token.CreateTokenRequest;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
@TestHTTPEndpoint(ApiTokenResource.class)
@TestSecurity(user = "alice", roles = "movie_group@sso.mymiggi.de")
class ApiTokenResourceTest
{
	@BeforeEach
	@AfterEach
	void clean()
	{
		QuarkusTransaction.begin();
		ApiTokenEntity.deleteAll();
		QuarkusTransaction.commit();
	}

	@Test
	void createReturnsSecretOnceWithSnapshottedUserRole()
	{
		given().contentType(ContentType.JSON)
			.body(new CreateTokenRequest("laptop"))
			.post()
			.then()
			.statusCode(200)
			.body("secret", startsWith("mvk_"))
			.body("name", is("laptop"))
			.body("role", is("USER"));
	}

	@Test
	@TestSecurity(user = "boss", roles = "movie_admins@sso.mymiggi.de")
	void adminCreatesAdminRoleToken()
	{
		given().contentType(ContentType.JSON)
			.body(new CreateTokenRequest("ci"))
			.post()
			.then()
			.statusCode(200)
			.body("role", is("ADMIN"));
	}

	@Test
	void blankNameIsRejected()
	{
		given().contentType(ContentType.JSON)
			.body(new CreateTokenRequest("  "))
			.post()
			.then()
			.statusCode(400);
	}

	@Test
	void listShowsOnlyOwnTokens()
	{
		given().contentType(ContentType.JSON).body(new CreateTokenRequest("alice-token")).post().then().statusCode(200);
		insertToken("bob-token", "bob", TokenRole.USER);

		given().get()
			.then()
			.statusCode(200)
			.body("size()", is(1))
			.body("[0].name", is("alice-token"));
	}

	@Test
	void deleteRevokesOwnToken()
	{
		long id = given().contentType(ContentType.JSON).body(new CreateTokenRequest("temp"))
			.post().then().statusCode(200).extract().jsonPath().getLong("id");

		given().pathParam("id", id).delete("{id}").then().statusCode(204);
		given().get().then().statusCode(200).body("size()", is(0));
	}

	@Test
	void cannotDeleteAnotherUsersToken()
	{
		long id = insertToken("bob-token", "bob", TokenRole.USER);
		given().pathParam("id", id).delete("{id}").then().statusCode(404);
	}

	private long insertToken(String name, String principal, TokenRole role)
	{
		QuarkusTransaction.begin();
		ApiTokenEntity entity = new ApiTokenEntity(name, TokenSecrets.hash(name + "-secret"), principal, role);
		entity.persist();
		QuarkusTransaction.commit();
		return entity.id;
	}
}
