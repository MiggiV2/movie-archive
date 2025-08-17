package de.mymiggi.movie.api;

import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
@TestSecurity(user = "test", roles = "movie_group@sso.mymiggi.de")
public class UserResourceTest
{
	@Inject
	DefaultPage defaultPage;

	@BeforeEach
	void setup()
	{
		QuarkusTransaction.begin();
		MovieEntity movieEntity = MovieEntity.findById(19);
		movieEntity.block = "Block 1";
		movieEntity.persist();
		QuarkusTransaction.commit();
	}

	@Test
	void testGetMovieListByPage()
	{
		given().when()
			.queryParam("page", 0)
			.get("get-movies")
			.then()
			.statusCode(200)
			.body("size()", is(defaultPage.Size()));
	}

	@Test
	void testGetMovieListByID()
	{
		given().when()
			.queryParam("id", 10)
			.get("get-movie-by-id")
			.then().statusCode(200)
			.body("year", is(2014))
			.body("title", is("Interstellar"))
			.body("block", is("J"))
			.body("wikiUrl", is("https://en.wikipedia.org/wiki/Interstellar_(film)"))
			.body("type", is("BD"));
	}

	@Test
	void testPageSize()
	{
		given().when()
			.queryParam("page", 2)
			.queryParam("desc", false)
			.get("preview-movies/by-year")
			.then()
			.statusCode(200)
			.body("size()", is(defaultPage.Size()));
	}

	@Test
	void testGetNameSortedMovies()
	{
		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", false)
			.get("preview-movies/by-name");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		MoviePreview lastMovie = movies[movies.length - 1];
		MoviePreview firstMovie = movies[0];
		assertTrue(lastMovie.getTitle().compareTo(firstMovie.getTitle()) > 0);
	}

	@Test
	void testGetNameSortedMoviesDesc()
	{
		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", true)
			.get("preview-movies/by-name");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		MoviePreview lastMovie = movies[movies.length - 1];
		MoviePreview firstMovie = movies[0];
		assertTrue(lastMovie.getTitle().compareTo(firstMovie.getTitle()) < 0);
	}

	@Test
	void testGetYearSortedMovies()
	{
		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", false)
			.get("preview-movies/by-year");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		MoviePreview lastMovie = movies[movies.length - 1];
		MoviePreview firstMovie = movies[0];
		assertTrue(lastMovie.getYear() - firstMovie.getYear() > 0);
	}

	@Test
	void testGetYearSortedMoviesDesc()
	{
		Response response = given().when()
			.queryParam("page", 2)
			.queryParam("desc", true)
			.get("preview-movies/by-year");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		MoviePreview lastMovie = movies[movies.length - 1];
		MoviePreview firstMovie = movies[0];
		assertTrue(lastMovie.getYear() - firstMovie.getYear() < 0);
	}

	@Test
	void testSearchMovie()
	{
		String query = "Jok";
		Response response = given().when()
			.queryParam("query", query)
			.get("search");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		assertEquals(1, movies.length);
		for (MoviePreview movie : movies)
		{
			if (!movie.getTitle().toLowerCase().contains(query.toLowerCase()))
			{
				System.err.println(movie.getTitle());
				fail("This movie does not contain the query word!");
			}
		}
	}

	@Test
	void testSearchMovieLowerCase()
	{
		String query = "jok";
		Response response = given().when()
			.queryParam("query", query)
			.get("search");

		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		assertEquals(1, movies.length);
		for (MoviePreview movie : movies)
		{
			if (!movie.getTitle().toLowerCase().contains(query.toLowerCase()))
			{
				System.err.println(movie.getTitle());
				fail("This movie does not contain the query word!");
			}
		}
	}

	@Test
	void testSearchMovieMoreWords()
	{
		long[] sortedIDs = { 3 };

		Response response = given().when()
			.queryParam("query", "Forrest")
			.get("search");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		compareIDs(sortedIDs, movies);
	}

	@Test
	void testSearchMovieMoreWords2()
	{
		long[] sortedIDs = { 7 };

		Response response = given().when()
			.queryParam("query", "The M")
			.get("search");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		compareIDs(sortedIDs, movies);
	}

	@Test
	void testSync()
	{
		Response response = given().when().get("sync");

		response.then().statusCode(200);
		Map<Long, String> hashes = response.body().as(HashResult.class);

		assertEquals(20, hashes.size());
		for (Long id : hashes.keySet())
		{
			assertEquals(32, hashes.get(id).length());
		}
	}

	@Test
	void slq()
	{
		List<MovieEntity> movies = MovieEntity.find("name ILIKE ?1 AND name ILIKE ?2", "%Silence%", "%Lambs%").list();
		assertEquals(1, movies.size());
	}

	private void compareIDs(long[] sortedIDs, MoviePreview[] movies)
	{
		assertEquals(movies.length, sortedIDs.length, "Expected different count of movies");
		for (int i = 0; i < sortedIDs.length; i++)
		{
			assertEquals(movies[i].getId(), sortedIDs[i], "Expected different movie id");
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
