package de.mymiggi.movie.api;

import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import de.mymiggi.movie.api.entity.db.TagEntity;
import de.mymiggi.movie.api.entity.db.TagMovieRelation;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
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
	@Inject
	Flyway flyway;

	@BeforeEach
	void resetDatabase()
	{
		flyway.clean();
		flyway.migrate();
	}

	@Test
	void testAddAndDelete()
	{
		long moviesBefore = MovieEntity.count();
		DetailedMovie movie = testAdd(moviesBefore);
		testDelete(moviesBefore, movie);
	}

	@Test
	void testUpdateOnlyMovieEntity()
	{
		MovieEntity entity = new MovieEntity(1999, "The Matrix", "G",
			"https://en.wikipedia.org/wiki/The_Matrix", "BD");
		entity.id = 7L;
		entity.originalName = "Matrix";
		DetailedMovie detailedMovie = new DetailedMovie(entity, new MovieMetaData());

		given()
			.when()
			.contentType(ContentType.JSON)
			.body(detailedMovie)
			.put("update-movie")
			.then()
			.statusCode(200)
			.body("title", is(entity.name))
			.body("id", is(entity.id.intValue()))
			.body("block", is(entity.block))
			.body("originalName", is(entity.originalName))
			.body("block", is(entity.block));
	}

	@Test
	void testUpdateOnlyMovieEntityWithExistingMetaData()
	{
		MovieEntity entity = new MovieEntity(1999, "The Matrix", "M",
			"https://en.wikipedia.org/wiki/The_Matrix", "BD");
		entity.id = 7L;
		entity.originalName = "Matrix";
		DetailedMovie detailedMovie = new DetailedMovie(entity, new MovieMetaData());
		detailedMovie.setExternalId("tt0133093");

		given()
			.when()
			.contentType(ContentType.JSON)
			.body(detailedMovie)
			.put("update-movie")
			.then()
			.statusCode(200)
			.body("title", is(entity.name))
			.body("id", is(entity.id.intValue()))
			.body("block", is(entity.block))
			.body("originalName", is(entity.originalName))
			.body("block", is(entity.block))
			.body("runtime", is(136));
	}

	@Test
	void testUpdateWithNewMetadata()
	{
		MovieEntity entity = new MovieEntity(2000, "Gladiator", "K",
			"https://en.wikipedia.org/wiki/Gladiator_(2000_film)", "BD");
		entity.id = 11L;
		entity.originalName = "Gladiator";
		MovieMetaData movieMetaData = new MovieMetaData();
		movieMetaData.setImdbId("tt0172495");
		DetailedMovie detailedMovie = new DetailedMovie(entity, movieMetaData);

		given()
			.when()
			.contentType(ContentType.JSON)
			.body(detailedMovie)
			.put("update-movie")
			.then()
			.statusCode(200)
			.body("title", is(entity.name))
			.body("id", is(entity.id.intValue()))
			.body("block", is(entity.block))
			.body("originalName", is(entity.originalName))
			.body("block", is(entity.block))
			.body("runtime", is(9300));
	}

	@Test
	void testTagsUpdate()
	{
		String[] tags = { "Action", "Mystery", "Test-Tag" };
		long movieId = 5;

		// Check count of tags before
		MovieEntity movieEntity = MovieEntity.findById(movieId);
		long savedTags = TagMovieRelation.find("movie", movieEntity).count();
		assertEquals(1, savedTags);

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
	void testAuditLogPageCount()
	{
		given()
			.when()
			.get("auditlog-page-count")
			.then()
			.statusCode(200)
			.body(is("4"));
	}

	@Test
	void testAuditLogPage()
	{
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

		MovieEntity firstMovie = MovieEntity.findAll(Sort.ascending("name")).firstResult();
		assertEquals("A1", firstMovie.block);

		MovieEntity lastMovie = MovieEntity.findAll(Sort.descending("name")).firstResult();
		assertEquals("B1", lastMovie.block);
	}

	private DetailedMovie testAdd(long moviesBefore)
	{
		MovieEntity entity = new MovieEntity(2025, "The Fantastic Four: First Steps", "Block1",
			"https://de.wikipedia.org/wiki/The_Fantastic_Four", "DB");
		entity.id = 0L;
		DetailedMovie detailedMovie = new DetailedMovie(entity, new MovieMetaData());

		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(detailedMovie)
			.post("add-movie");

		response.then().statusCode(200);
		DetailedMovie movie = response.body().as(DetailedMovie.class);

		assertEquals(entity.name, movie.getTitle());
		assertNotEquals(entity.id, movie.getId());
		assertNotEquals(0, movie.getRuntime());
		assertEquals(moviesBefore + 1, MovieEntity.count());
		return movie;
	}

	private void testDelete(long moviesBefore, DetailedMovie movie)
	{
		Response delResponse = given()
			.when()
			.contentType(ContentType.JSON)
			.queryParam("id", movie.getId())
			.delete("delete-movie");

		delResponse.then().statusCode(204);
		assertEquals(moviesBefore, MovieEntity.count());
	}
}