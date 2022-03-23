package de.mymiggi;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ArchiveResourceTest
{

	@Test
	public void testHelloEndpoint()
	{
		/*
		 * given() .queryParam("page", "0")
		 * .when().get("movie-archive/user/get-movies") .then()
		 * .statusCode(200); given() .queryParam("page", "1")
		 * .when().get("movie-archive/user/get-movies") .then()
		 * .statusCode(200); given() .queryParam("page", "-1")
		 * .when().get("movie-archive/user/get-movies") .then()
		 * .statusCode(404); given() .queryParam("page", "999")
		 * .when().get("movie-archive/user/get-movies") .then()
		 * .statusCode(404);
		 */
	}

}