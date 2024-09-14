package de.mymiggi;

import de.mymiggi.movie.api.AdminResource;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.TagEntity;
import de.mymiggi.movie.api.entity.db.TagMovieRelation;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@QuarkusTest
@TestHTTPEndpoint(AdminResource.class)
@TestSecurity(user = "admin", roles = "admin")
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
}
