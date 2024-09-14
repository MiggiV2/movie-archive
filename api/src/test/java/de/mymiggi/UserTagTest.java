package de.mymiggi;

import de.mymiggi.movie.api.UserResource;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.TagEntity;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
@TestSecurity(user = "test", roles = "admin")
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
			new TagEntity("Action", null), new TagEntity("Disaster", null),
			new TagEntity("Fantasy", null), new TagEntity("Romance", null),
			new TagEntity("Sci-Fi", null), new TagEntity("Thriller", null),
			new TagEntity("Adventure", null), new TagEntity("Animation", null),
			new TagEntity("Mystery", null), new TagEntity("Horror", null) };

		assertEquals(10, tags.length);
		for (int i = 0; i < expectedTags.length; i++)
		{
			assertEquals(expectedTags[i].getName(), tags[i].getName());
		}
	}

	@Test
	void testTagsEmpty()
	{
		long movieId = 2;
		Response response = given().when()
			.get("tags/by-movie/" + movieId);

		response.then().statusCode(200);
		TagEntity[] tags = response.body().as(TagEntity[].class);
		assertEquals(0, tags.length);
	}

	@Test
	void testTagList()
	{
		Response response = given().when().get("tags");

		response.then().statusCode(200);
		TagEntity[] tags = response.body().as(TagEntity[].class);

		assertEquals(11, tags.length);
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