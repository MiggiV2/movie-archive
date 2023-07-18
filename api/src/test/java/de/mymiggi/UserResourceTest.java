package de.mymiggi;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mymiggi.movie.api.UserResource;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
@TestSecurity(user = "test", roles = "user")
public class UserResourceTest
{
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Inject
	DefaultPage defaultPage;

	@Test
	public void testGetMovieListByPage() throws JsonProcessingException
	{
		Response response = given().when()
			.queryParam("page", 0)
			.get("get-movies");

		response.then().statusCode(200);
		String body = response.body().asString();
		MovieEntity[] movies = MAPPER.readValue(body, MovieEntity[].class);

		assertEquals(defaultPage.Size(), movies.length);
	}

	@Test
	public void testGetMovieListByID() throws JsonProcessingException
	{
		Response response = given().when()
			.queryParam("id", 19)
			.get("get-movie-by-id");

		response.then().statusCode(200);
		String body = response.body().asString();
		MovieEntity movie = MAPPER.readValue(body, MovieEntity.class);

		assertEquals(movie.id, 19);
		assertEquals(movie.year, 2012);
		assertEquals(movie.name, "Alien - Prometheus - Dunkle Zeichen");
		assertEquals(movie.uuid, "A8");
		assertEquals(movie.block, "Block 1");
		assertEquals(movie.wikiUrl, "https://de.wikipedia.org/wiki/Prometheus_%E2%80%93_Dunkle_Zeichen");
		assertEquals(movie.type, "BD");
	}

	@Test
	public void testGetNameSortedMovies() throws JsonProcessingException
	{
		long[] sortedIDs = { 62, 63, 64, 65, 66, 67, 68, 70, 69, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83,
			85, 87, 84, 86, 88, 89, 90, 91 };

		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", false)
			.get("sorted-movies/by-name");

		response.then().statusCode(200);
		compareIDs(sortedIDs, response.body().asString());
	}

	@Test
	public void testGetNameSortedMoviesDesc() throws JsonProcessingException
	{
		long[] sortedIDs = { 303, 302, 301, 300, 299, 298, 294, 293, 297, 296, 295, 292, 291, 290, 289, 288, 287, 286,
			285, 284, 283, 275, 274, 276, 278, 281, 273, 279, 277, 272 };

		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", true)
			.get("sorted-movies/by-name");

		response.then().statusCode(200);
		compareIDs(sortedIDs, response.body().asString());
	}

	@Test
	public void testGetYearSortedMovies() throws JsonProcessingException
	{
		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", false)
			.get("sorted-movies/by-year");

		response.then().statusCode(200);
		MovieEntity[] movies = MAPPER.readValue(response.body().asString(), MovieEntity[].class);
		assertEquals(movies.length, defaultPage.Size());

		int lastYear = movies[0].year;
		for (MovieEntity movie : movies)
		{
			assertTrue(movie.year >= lastYear);
			if (movie.year > lastYear)
			{
				lastYear = movie.year;
			}
		}
	}

	@Test
	public void testGetYearSortedMoviesDesc() throws JsonProcessingException
	{
		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", true)
			.get("sorted-movies/by-year");

		response.then().statusCode(200);
		MovieEntity[] movies = MAPPER.readValue(response.body().asString(), MovieEntity[].class);
		assertEquals(movies.length, defaultPage.Size());

		int lastYear = movies[0].year;
		for (MovieEntity movie : movies)
		{
			assertTrue(movie.year <= lastYear);
			if (movie.year < lastYear)
			{
				lastYear = movie.year;
			}
		}
	}

	@Test
	public void testSearchMovie() throws JsonProcessingException
	{
		long[] sortedIDs = { 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 324, 353 };

		Response response = given().when()
			.queryParam("query", "Star")
			.get("search");

		response.then().statusCode(200);
		compareIDs(sortedIDs, response.body().asString());
	}

	@Test
	public void testSearchMovieLowerCase() throws JsonProcessingException
	{
		long[] sortedIDs = { 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 324, 353 };

		Response response = given().when()
			.queryParam("query", "star")
			.get("search");

		response.then().statusCode(200);
		compareIDs(sortedIDs, response.body().asString());
	}

	@Test
	public void testSearchMovieMoreWords() throws JsonProcessingException
	{
		long[] sortedIDs = { 273, 278, 270, 271, 272, 274, 275, 276, 277, 279, 280, 281, 282, 324, 353 };

		Response response = given().when()
			.queryParam("query", "star jedi")
			.get("search");

		response.then().statusCode(200);
		compareIDs(sortedIDs, response.body().asString());
	}

	private void compareIDs(long[] sortedIDs, String jsonResponse) throws JsonProcessingException
	{
		MovieEntity[] movies = MAPPER.readValue(jsonResponse, MovieEntity[].class);
		assertEquals(movies.length, sortedIDs.length);
		for (int i = 0; i < sortedIDs.length; i++)
		{
			assertEquals(movies[i].id, sortedIDs[i]);
		}
	}
}
