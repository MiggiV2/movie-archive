package de.mymiggi.movie.api;

import de.mymiggi.movie.api.auth.TokenSecrets;
import de.mymiggi.movie.api.entity.TokenRole;
import de.mymiggi.movie.api.entity.db.ApiTokenEntity;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Exercises the real {@link de.mymiggi.movie.api.auth.TokenAuthenticationMechanism} end to
 * end (no {@code @TestSecurity}); {@code %test} keeps the production permission policies, so
 * a Bearer token must actually authenticate and carry its snapshotted role.
 */
@QuarkusTest
class TokenAuthenticationTest
{
	private String userSecret;
	private String adminSecret;

	@BeforeEach
	void seed()
	{
		QuarkusTransaction.begin();
		ApiTokenEntity.deleteAll();
		userSecret = TokenSecrets.generateSecret();
		adminSecret = TokenSecrets.generateSecret();
		new ApiTokenEntity("user-tok", TokenSecrets.hash(userSecret), "alice", TokenRole.USER).persist();
		new ApiTokenEntity("admin-tok", TokenSecrets.hash(adminSecret), "boss", TokenRole.ADMIN).persist();
		QuarkusTransaction.commit();
	}

	@AfterEach
	void clean()
	{
		QuarkusTransaction.begin();
		ApiTokenEntity.deleteAll();
		QuarkusTransaction.commit();
	}

	@Test
	void validTokenAuthenticatesReadRequest()
	{
		given().header("Authorization", "Bearer " + userSecret)
			.queryParam("page", 0)
			.get("/api/v2/movie")
			.then()
			.statusCode(200);
	}

	@Test
	void userTokenIsForbiddenOnAdminEndpoint()
	{
		given().header("Authorization", "Bearer " + userSecret)
			.queryParam("id", 1)
			.delete("/api/v2/movie")
			.then()
			.statusCode(403);
	}

	@Test
	void adminTokenIsAllowedOnAdminEndpoint()
	{
		String[] tags = { "TokenTag" };
		given().header("Authorization", "Bearer " + adminSecret)
			.contentType("application/json")
			.body(tags)
			.pathParam("movie-id", 278)
			.post("/api/v2/movie/{movie-id}/tags")
			.then()
			.statusCode(204);
	}

	@Test
	void unknownTokenIsRejected()
	{
		given().header("Authorization", "Bearer mvk_unknownsecretvalue")
			.queryParam("page", 0)
			.get("/api/v2/movie")
			.then()
			.statusCode(401);
	}

	@Test
	void revokedTokenIsRejected()
	{
		QuarkusTransaction.begin();
		ApiTokenEntity.delete("principal", "alice");
		QuarkusTransaction.commit();

		given().header("Authorization", "Bearer " + userSecret)
			.queryParam("page", 0)
			.get("/api/v2/movie")
			.then()
			.statusCode(401);
	}
}
