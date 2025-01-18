package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.entity.db.AuditLogEntity;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.TagEntity;
import de.mymiggi.movie.api.entity.db.TagMovieRelation;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@QuarkusTest
@TestHTTPEndpoint(AdminResource.class)
@TestSecurity(user = "admin", roles = "movie_admins@sso.mymiggi.de")
public class AdminResourceTest
{
	@Test
	void testAddAndDelete()
	{
		long moviesBefore = MovieEntity.count();
		MovieEntity movie = testAdd(moviesBefore);
		testDelete(moviesBefore, movie);
	}

	@Test
	void testUpdate()
	{
		MovieEntity entity = new MovieEntity(2015, "Maggie", "Block 8",
			"https://de.wikipedia.org/wiki/Maggie_(2015)", "BD");
		entity.id = 223L;

		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(entity)
			.put("update-movie");

		response.then().statusCode(200);
		MovieEntity movie = response.body().as(MovieEntity.class);

		assertEquals(entity.name, movie.name);
		assertEquals(entity.id, movie.id);
		assertNotEquals(movie.block, "Block 7");
	}

	@Test
	void testTagsUpdate()
	{
		String[] tags = { "Action", "Mystery", "Test-Tag" };
		long movieId = 5;

		// Check count of tags before
		MovieEntity movieEntity = MovieEntity.findById(movieId);
		long savedTags = TagMovieRelation.find("movie", movieEntity).count();
		assertEquals(6, savedTags);

		// Updates
		given()
			.when()
			.contentType(ContentType.JSON)
			.body(tags)
			.post("tag-movie/" + movieId)
			.then()
			.statusCode(204);

		// Check count of tags - after
		savedTags = TagMovieRelation.find("movie", movieEntity).count();
		long existingTags = TagEntity.count();
		assertEquals(3, savedTags);
		assertEquals(11, existingTags);
	}

	@Test
	@Transactional
	void test0AuditLogPageCount()
	{
		AuditLogEntity.deleteAll();

		given()
			.when()
			.get("auditlog-page-count")
			.then()
			.statusCode(200)
			.body(is("0"));
	}

	@Test
	@Transactional
	void testAuditLogPageCount()
	{
		createDummyLog();

		given()
			.when()
			.get("auditlog-page-count")
			.then()
			.statusCode(200)
			.body(is("0"));
	}

	@Test
	@Transactional
	void testAuditLogPage()
	{
		createDummyLog();

		given()
			.when()
			.queryParam("page", "0")
			.get("auditlog")
			.then()
			.statusCode(200);
	}

	@Test
	void testBlockUpdate()
	{
		given()
			.when()
			.contentType(ContentType.JSON)
			.post("trigger/block-update")
			.then()
			.statusCode(204);

		MovieEntity lastMovie = MovieEntity.findAll(Sort.descending("name")).firstResult();
		assertEquals("C3", lastMovie.block);

		MovieEntity firstMovie = MovieEntity.findAll(Sort.ascending("name")).firstResult();
		assertEquals("A1", firstMovie.block);
	}

	private MovieEntity testAdd(long moviesBefore)
	{
		MovieEntity entity = new MovieEntity(2077, "Cyberpunk 2077", "Block1",
			"https://de.wikipedia.org/wiki/Cyberpunk2077", "DB");

		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(entity)
			.post("add-movie");

		response.then().statusCode(200);
		MovieEntity movie = response.body().as(MovieEntity.class);

		assertEquals(entity.name, movie.name);
		assertEquals(moviesBefore + 1, MovieEntity.count());
		return movie;
	}

	private void testDelete(long moviesBefore, MovieEntity movie)
	{
		Response delResponse = given()
			.when()
			.contentType(ContentType.JSON)
			.queryParam("id", movie.id)
			.delete("delete-movie");

		delResponse.then().statusCode(204);
		assertEquals(moviesBefore, MovieEntity.count());
	}

	private void createDummyLog()
	{
		MovieEntity movie = new MovieEntity();
		movie.id = 0L;
		AuditLogEntity auditLog = new AuditLogEntity("test-user", new AddMovieAction(), "Added movie", movie);
		auditLog.persist();
	}
}