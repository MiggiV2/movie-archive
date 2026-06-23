package de.mymiggi.movie.api.mcp;

import de.mymiggi.movie.api.auth.TokenSecrets;
import de.mymiggi.movie.api.entity.TokenRole;
import de.mymiggi.movie.api.entity.db.ApiTokenEntity;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import io.quarkiverse.mcp.server.JsonRpcErrorCodes;
import io.quarkiverse.mcp.server.test.McpAssured;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.MultiMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MCP authentication and authorization: read/write tools follow the token's snapshotted role,
 * write tools are admin-only (MCP error -32001 otherwise), and missing/invalid/revoked tokens
 * are rejected at connect time. Also verifies write tools are audit-logged to the token owner.
 */
@QuarkusTest
class MovieToolsAuthTest
{
	private static final String CREATED_TITLE = "MCP Created Movie";

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
		List<MovieEntity> created = MovieEntity.list("name", CREATED_TITLE);
		for (MovieEntity movie : created)
		{
			MovieMetaData.delete("movieEntity", movie);
			movie.delete();
		}
		QuarkusTransaction.commit();
	}

	private io.quarkiverse.mcp.server.test.McpAssured.McpStreamableTestClient connect(String secret)
	{
		return McpAssured.newStreamableClient()
			.setMcpPath("/mcp")
			.setAdditionalHeaders(message -> MultiMap.caseInsensitiveMultiMap().add("Authorization", "Bearer " + secret))
			.build()
			.connect();
	}

	@Test
	void userTokenCannotCallWriteTool()
	{
		connect(userSecret).when()
			.toolsCall("create_movie")
			.withArguments(Map.of("title", CREATED_TITLE, "type", "BD", "year", 2020))
			.withErrorAssert(error -> assertEquals(JsonRpcErrorCodes.SECURITY_ERROR, error.code()))
			.send()
			.thenAssertResults();

		assertEquals(0, MovieEntity.count("name", CREATED_TITLE));
	}

	@Test
	void adminTokenCanCreateMovieAndItIsAudited()
	{
		long auditBefore = AuditLogEntity.count("userName = ?1 and auditLogType = ?2", "boss", "add");

		connect(adminSecret).when()
			.toolsCall("create_movie", Map.of("title", CREATED_TITLE, "type", "BD", "year", 2021),
				response -> assertTrue(response.firstContent().asText().text().contains(CREATED_TITLE)))
			.thenAssertResults();

		assertTrue(MovieEntity.count("name", CREATED_TITLE) >= 1, "movie should be created");
		long auditAfter = AuditLogEntity.count("userName = ?1 and auditLogType = ?2", "boss", "add");
		assertEquals(auditBefore + 1, auditAfter, "create should be audit-logged to the token owner");
	}

	@Test
	void missingTokenIsRejected()
	{
		McpAssured.newStreamableClient()
			.setMcpPath("/mcp")
			.setExpectConnectFailure()
			.build()
			.connect();
	}

	@Test
	void invalidTokenIsRejected()
	{
		McpAssured.newStreamableClient()
			.setMcpPath("/mcp")
			.setAdditionalHeaders(message -> MultiMap.caseInsensitiveMultiMap().add("Authorization", "Bearer mvk_invalidsecretvalue"))
			.setExpectConnectFailure()
			.build()
			.connect();
	}

	@Test
	void revokedTokenIsRejected()
	{
		QuarkusTransaction.begin();
		ApiTokenEntity.delete("principal", "alice");
		QuarkusTransaction.commit();

		McpAssured.newStreamableClient()
			.setMcpPath("/mcp")
			.setAdditionalHeaders(message -> MultiMap.caseInsensitiveMultiMap().add("Authorization", "Bearer " + userSecret))
			.setExpectConnectFailure()
			.build()
			.connect();
	}
}
