package de.mymiggi;

import de.mymiggi.movie.api.UserResource;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
@TestSecurity(user = "test", roles = "user")
public class UserResourceTest
{
	@Inject
	DefaultPage defaultPage;

	@Test
	void testGetMovieListByPage()
	{
		Response response = given().when()
			.queryParam("page", 0)
			.get("get-movies");

		response.then().statusCode(200);
		MovieEntity[] movies = response.body().as(MovieEntity[].class);
		assertEquals(defaultPage.Size(), movies.length);
	}

	@Test
	void testGetMovieListByID()
	{
		Response response = given().when()
			.queryParam("id", 19)
			.get("get-movie-by-id");

		response.then().statusCode(200);
		MovieEntity movie = response.body().as(MovieEntity.class);

		assertEquals(movie.id, 19);
		assertEquals(movie.year, 2012);
		assertEquals(movie.name, "Alien - Prometheus - Dunkle Zeichen");
		assertEquals(movie.uuid, "A8");
		assertEquals(movie.block, "Block 1");
		assertEquals(movie.wikiUrl, "https://de.wikipedia.org/wiki/Prometheus_%E2%80%93_Dunkle_Zeichen");
		assertEquals(movie.type, "BD");
	}

	@Test
	void testGetNameSortedMovies()
	{
		long[] sortedIDs = { 62, 63, 64, 65, 66, 67, 68, 70, 69, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83,
			85, 87, 84, 86, 88, 89, 90, 91 };

		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", false)
			.get("sorted-movies/by-name");

		response.then().statusCode(200);
		compareIDs(sortedIDs, response.body().as(MovieEntity[].class));
	}

	@Test
	void testGetNameSortedMoviesDesc()
	{
		long[] sortedIDs = { 303, 302, 301, 300, 299, 298, 294, 293, 297, 296, 295, 292, 291, 290, 289, 288, 287, 286,
			285, 284, 283, 275, 274, 276, 278, 281, 273, 279, 277, 272 };

		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", true)
			.get("sorted-movies/by-name");

		response.then().statusCode(200);
		compareIDs(sortedIDs, response.body().as(MovieEntity[].class));
	}

	@Test
	void testGetYearSortedMovies()
	{
		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", false)
			.get("sorted-movies/by-year");

		response.then().statusCode(200);
		MovieEntity[] movies = response.body().as(MovieEntity[].class);
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
	void testGetYearSortedMoviesDesc()
	{
		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", true)
			.get("sorted-movies/by-year");

		response.then().statusCode(200);
		MovieEntity[] movies = response.body().as(MovieEntity[].class);
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
	void testSearchMovie()
	{
		long[] sortedIDs = { 324, 282, 270, 271, 280, 272, 277, 279, 273, 281, 278, 276, 274, 275 };

		Response response = given().when()
			.queryParam("query", "Star")
			.get("search");

		response.then().statusCode(200);
		compareIDs(sortedIDs, response.body().as(MovieEntity[].class));
	}

	@Test
	void testSearchMovieLowerCase()
	{
		long[] sortedIDs = { 324, 282, 270, 271, 280, 272, 277, 279, 273, 281, 278, 276, 274, 275 };

		Response response = given().when()
			.queryParam("query", "star")
			.get("search");

		response.then().statusCode(200);
		compareIDs(sortedIDs, response.body().as(MovieEntity[].class));
	}

	@Test
	void testSearchMovieMoreWords()
	{
		long[] sortedIDs = { 273, 278 };

		Response response = given().when()
			.queryParam("query", "Star Jedi")
			.get("search");

		response.then().statusCode(200);
		MovieEntity[] movies = response.body().as(MovieEntity[].class);
		compareIDs(sortedIDs, movies);
	}

	@Test
	void testSearchMovieMoreWords2()
	{
		long[] sortedIDs = { 178, 185, 186 };

		Response response = given().when()
			.queryParam("query", "Bond Gold")
			.get("search");

		response.then().statusCode(200);
		MovieEntity[] movies = response.body().as(MovieEntity[].class);
		compareIDs(sortedIDs, movies);
	}

	@Test
	void testSync()
	{
		Response response = given().when().get("sync");

		response.then().statusCode(200);
		Map<Long, String> hashes = response.body().as(HashResult.class);

		assertEquals(hashes.size(), 363);
		for (Long id : hashes.keySet())
		{
			assertEquals(hashes.get(id).length(), 32);
		}
	}

	@Test
	void slq()
	{
		List<MovieEntity> movies = MovieEntity.find("name ILIKE ?1 AND name ILIKE ?2", "%Star%", "%Jedi%").list();
		assertEquals(movies.size(), 2);
	}

	private void compareIDs(long[] sortedIDs, MovieEntity[] movies)
	{
		assertEquals(movies.length, sortedIDs.length, "Expected different count of movies");
		for (int i = 0; i < sortedIDs.length; i++)
		{
			assertEquals(movies[i].id, sortedIDs[i], "Expected different movie id");
		}
	}

	// Used for sync endpoint
	static class HashResult extends HashMap<Long, String>
	{
		public HashResult()
		{
		}

		public HashResult(Map<Long, String> map)
		{
			this.putAll(map);
		}
	}
}
