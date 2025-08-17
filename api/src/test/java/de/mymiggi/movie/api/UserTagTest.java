package de.mymiggi.movie.api;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.TagEntity;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
@TestSecurity(user = "test", roles = "movie_group@sso.mymiggi.de")
public class UserTagTest
{

	@Test
	void testTagsSuccessful()
	{
		long movieId = 1;
		Response response = given().when()
			.get("tags/by-movie/" + movieId);

		response.then().statusCode(200);
		TagEntity[] tags = response.body().as(TagEntity[].class);
		TagEntity[] expectedTags = {
			new TagEntity("Oscar Winner", null), new TagEntity("Cult Classic", null),
			new TagEntity("Top Rated", null), new TagEntity("Drama", null) };

		assertEquals(expectedTags.length, tags.length);
		for (int i = 0; i < expectedTags.length; i++)
		{
			assertEquals(expectedTags[i].getName(), tags[i].getName());
		}
	}

	@Test
	void testTagsEmpty()
	{
		given()
			.when()
			.get("tags/by-movie/" + 19)
			.then()
			.statusCode(200)
			.body("size()", is(0));
	}

	@Test
	void testTagList()
	{
		given()
			.when()
			.get("tags")
			.then()
			.statusCode(200)
			.body("size()", is(10));
	}

	@Test
	void testMoviesByTag()
	{
		long tagId = 1;
		Response response = given().when()
			.get("tags/" + tagId);

		response.then().statusCode(200);
		MovieEntity[] movies = response.body().as(MovieEntity[].class);
		assertEquals(2, movies.length);
	}
}