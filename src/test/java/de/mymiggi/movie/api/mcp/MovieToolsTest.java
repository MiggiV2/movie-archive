package de.mymiggi.movie.api.mcp;

import de.mymiggi.movie.api.auth.TokenSecrets;
import de.mymiggi.movie.api.entity.TokenRole;
import de.mymiggi.movie.api.entity.db.ApiTokenEntity;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import io.quarkiverse.mcp.server.test.McpAssured;
import io.quarkiverse.mcp.server.test.McpAssured.McpStreamableTestClient;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.MultiMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tool discovery and read-tool behavior over the MCP Streamable HTTP transport, authenticated
 * with an API token via the {@code add-api-tokens} Bearer mechanism.
 */
@QuarkusTest
class MovieToolsTest
{
	private static final String TEST_GENRE = "McpTestGenre";
	private static final String TEST_MOVIE = "Genre Test Movie";

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

		// Deterministic movie + metadata carrying a known genre for the genre tools.
		MovieEntity movie = new MovieEntity(2019, TEST_MOVIE, "BlockX", "https://example.test", "BD");
		movie.persist();
		MovieMetaData meta = new MovieMetaData();
		meta.setGenres(List.of(TEST_GENRE));
		meta.setMovieEntity(movie);
		meta.persist();
		QuarkusTransaction.commit();
	}

	@AfterEach
	void clean()
	{
		QuarkusTransaction.begin();
		ApiTokenEntity.deleteAll();
		for (MovieEntity movie : MovieEntity.<MovieEntity>list("name", TEST_MOVIE))
		{
			MovieMetaData.delete("movieEntity", movie);
			movie.delete();
		}
		QuarkusTransaction.commit();
	}

	private McpStreamableTestClient connect(String secret)
	{
		return McpAssured.newStreamableClient()
			.setMcpPath("/mcp")
			.setAdditionalHeaders(message -> MultiMap.caseInsensitiveMultiMap().add("Authorization", "Bearer " + secret))
			.build()
			.connect();
	}

	@Test
	void discoversAllTools()
	{
		connect(adminSecret).when()
			.toolsList(page -> {
				assertNotNull(page.findByName("search_movies"));
				assertNotNull(page.findByName("list_genres"));
				assertNotNull(page.findByName("find_movies_by_genre"));
				assertNotNull(page.findByName("get_movie"));
				assertNotNull(page.findByName("create_movie"));
				assertNotNull(page.findByName("update_movie"));
			})
			.thenAssertResults();
	}

	@Test
	void searchMoviesFindsByTitle()
	{
		connect(userSecret).when()
			.toolsCall("search_movies", Map.of("query", "Oppenheimer"),
				response -> assertTrue(response.firstContent().asText().text().contains("Oppenheimer")))
			.thenAssertResults();
	}

	@Test
	void getMovieReturnsDetail()
	{
		connect(userSecret).when()
			.toolsCall("get_movie", Map.of("id", 278),
				response -> assertTrue(response.firstContent().asText().text().contains("Oppenheimer")))
			.thenAssertResults();
	}

	@Test
	void getMovieUnknownIdReturnsNotFoundResult()
	{
		connect(userSecret).when()
			.toolsCall("get_movie", Map.of("id", 999999),
				response -> assertTrue(response.firstContent().asText().text().contains("No movie found")))
			.thenAssertResults();
	}

	@Test
	void listGenresReturnsGenres()
	{
		// list_genres returns one TextContent item per genre string, so we must scan all
		// content items — not just the first one (which would be the first genre alphabetically).
		connect(userSecret).when()
			.toolsCall("list_genres",
				response -> assertTrue(
					response.content().stream()
						.map(c -> c.asText().text())
						.anyMatch(t -> t.contains(TEST_GENRE)),
					"Expected genre '" + TEST_GENRE + "' in response"))
			.thenAssertResults();
	}

	@Test
	void findMoviesByGenreIsCaseInsensitive()
	{
		connect(userSecret).when()
			.toolsCall("find_movies_by_genre", Map.of("genre", "mcptestgenre"),
				response -> assertTrue(response.firstContent().asText().text().contains(TEST_MOVIE)))
			.thenAssertResults();
	}
}
