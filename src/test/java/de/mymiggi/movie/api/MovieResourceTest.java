package de.mymiggi.movie.api;

import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.MoviePreview;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.entity.db.MovieMetaData;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@TestHTTPEndpoint(MovieResource.class)
@TestSecurity(user = "test", roles = "movie_group@sso.mymiggi.de")
public class MovieResourceTest
{
	@Inject
	DefaultPage defaultPage;

	@BeforeEach
	void setup()
	{
		QuarkusTransaction.begin();
		MovieEntity movieEntity = MovieEntity.findById(278L);
		if (movieEntity != null)
		{
			movieEntity.block = "Block C3 - 20";
			movieEntity.name = "Oppenheimer";
			movieEntity.persist();
		}
		QuarkusTransaction.commit();
	}

	@Test
	void testCount()
	{
		given().when()
			.get("count")
			.then()
			.statusCode(200)
			.body(is(String.valueOf(MovieEntity.count())));
	}

	@Test
	void testGetMovieListByPageOne()
	{
		given().when()
			.queryParam("page", 1)
			.get()
			.then()
			.statusCode(200);
	}

	@Test
	void testGetMovieListByPage()
	{
		given().when()
			.queryParam("page", 0)
			.get()
			.then()
			.statusCode(200)
			.body("size()", is(defaultPage.Size()));
	}

	@Test
	void testGetMovieListByID()
	{
		given().when()
			.pathParam("movie-id", 278)
			.get("{movie-id}")
			.then().statusCode(200)
			.body("year", is(2023))
			.body("title", is("Oppenheimer"))
			.body("block", is("Block C3 - 20"))
			.body("wikiUrl", is("https://de.wikipedia.org/wiki/Oppenheimer_(2023)"))
			.body("type", is("BD"));
	}

	@Test
	void testPageSize()
	{
		given().when()
			.queryParam("page", 0)
			.queryParam("desc", false)
			.get("preview/by-year")
			.then()
			.statusCode(200)
			.body("size()", is(defaultPage.Size()));
	}

	@Test
	void testGetNameSortedMovies()
	{
		Response response = given().when()
			.queryParam("page", 0)
			.queryParam("desc", false)
			.get("preview/by-name");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		if (movies.length > 1)
		{
			MoviePreview lastMovie = movies[movies.length - 1];
			MoviePreview firstMovie = movies[0];
			assertTrue(lastMovie.getTitle().compareTo(firstMovie.getTitle()) >= 0);
		}
	}

	@Test
	void testGetNameSortedMoviesDesc()
	{
		Response response = given().when()
			.queryParam("page", 0)
			.queryParam("desc", true)
			.get("preview/by-name");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		if (movies.length > 1)
		{
			MoviePreview lastMovie = movies[movies.length - 1];
			MoviePreview firstMovie = movies[0];
			assertTrue(lastMovie.getTitle().compareTo(firstMovie.getTitle()) <= 0);
		}
	}

	@Test
	void testGetYearSortedMovies()
	{
		Response response = given().when()
			.queryParam("page", 0)
			.queryParam("desc", false)
			.get("preview/by-year");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		if (movies.length > 1)
		{
			MoviePreview lastMovie = movies[movies.length - 1];
			MoviePreview firstMovie = movies[0];
			assertTrue(lastMovie.getYear() - firstMovie.getYear() >= 0);
		}
	}

	@Test
	void testGetYearSortedMoviesDesc()
	{
		Response response = given().when()
			.queryParam("page", 0)
			.queryParam("desc", true)
			.get("preview/by-year");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		if (movies.length > 1)
		{
			MoviePreview lastMovie = movies[movies.length - 1];
			MoviePreview firstMovie = movies[0];
			assertTrue(lastMovie.getYear() - firstMovie.getYear() <= 0);
		}
	}

	@Test
	void testSearchMovie()
	{
		String query = "Mine";
		Response response = given().when()
			.queryParam("query", query)
			.get("search");

		response.then().statusCode(200);
		MoviePreview[] movies = response.body().as(MoviePreview[].class);
		assertTrue(movies.length >= 1);
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
	@TestSecurity(user = "admin", roles = "movie_admins@sso.mymiggi.de")
	void testAddAndListTags()
	{
		long movieId = 278L;
		String[] tags = { "TestTag1", "TestTag2" };

		given().contentType("application/json")
			.body(tags)
			.pathParam("movie-id", movieId)
			.post("{movie-id}/tags")
			.then()
			.statusCode(204);

		given().when()
			.pathParam("movie-id", movieId)
			.get("{movie-id}/tags")
			.then()
			.statusCode(200)
			.body("size()", is(2));
	}

	@Test
	@TestSecurity(user = "admin", roles = "movie_admins@sso.mymiggi.de")
	void testAddAndDelete()
	{
		long moviesBefore = MovieEntity.count();
		DetailedMovie movie = testAdd(moviesBefore);
		testDelete(moviesBefore, movie);
	}

	@Test
	@TestSecurity(user = "admin", roles = "movie_admins@sso.mymiggi.de")
	void testUpdateMovie()
	{
		MovieEntity entity = MovieEntity.findById(278L);
		if (entity != null)
		{
			DetailedMovie detailedMovie = new DetailedMovie(entity, new MovieMetaData());
			detailedMovie.setTitle("Oppenheimer Updated");

			given()
				.when()
				.contentType(ContentType.JSON)
				.body(detailedMovie)
				.put()
				.then()
				.statusCode(200)
				.body("title", is("Oppenheimer Updated"));
		}
	}

	private DetailedMovie testAdd(long moviesBefore)
	{
		MovieEntity entity = new MovieEntity(2025, "New Movie", "Block1", "url", "BD");
		entity.id = 0L;
		DetailedMovie detailedMovie = new DetailedMovie(entity, new MovieMetaData());

		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(detailedMovie)
			.post();

		response.then().statusCode(200);
		DetailedMovie movie = response.body().as(DetailedMovie.class);

		assertEquals("New Movie", movie.getTitle());
		assertEquals(moviesBefore + 1, MovieEntity.count());
		return movie;
	}

	private void testDelete(long moviesBefore, DetailedMovie movie)
	{
		given()
			.when()
			.queryParam("id", movie.getId())
			.delete()
			.then()
			.statusCode(204);

		assertEquals(moviesBefore, MovieEntity.count());
	}
}
