package de.mymiggi;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mymiggi.movie.api.AdminResource;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@QuarkusTest
@TestHTTPEndpoint(AdminResource.class)
@TestSecurity(user = "admin", roles = "admin")
public class AdminResourceTest
{
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Test
	public void testAddAndDelete() throws JsonProcessingException
	{
		long moviesBefore = MovieEntity.count();
		MovieEntity movie = testAdd(moviesBefore);
		testDelete(moviesBefore, movie);
	}

	@Test
	public void testUpdate() throws JsonProcessingException
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
		String body = response.body().asString();
		MovieEntity movie = MAPPER.readValue(body, MovieEntity.class);

		assertEquals(entity.name, movie.name);
		assertEquals(entity.id, movie.id);
		assertNotEquals(movie.block, "Block 7");
	}

	private MovieEntity testAdd(long moviesBefore) throws JsonProcessingException
	{
		MovieEntity entity = new MovieEntity(2077, "Cyberpunk 2077", "Block1",
			"https://de.wikipedia.org/wiki/Cyberpunk2077", "DB");

		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(entity)
			.post("add-movie");

		response.then().statusCode(200);
		String body = response.body().asString();
		MovieEntity movie = MAPPER.readValue(body, MovieEntity.class);

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
