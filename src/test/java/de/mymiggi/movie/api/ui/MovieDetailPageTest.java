package de.mymiggi.movie.api.ui;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.mymiggi.movie.api.entity.db.MovieEntity;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@QuarkusTest
@TestSecurity(user = "test", roles = "movie_group@sso.mymiggi.de")
class MovieDetailPageTest
{
	private static final long MOVIE_ID = 278L;

	@BeforeEach
	void setup()
	{
		QuarkusTransaction.begin();
		MovieEntity movieEntity = MovieEntity.findById(MOVIE_ID);
		movieEntity.name = "Der Herr der Ringe – Die zwei Türme";
		movieEntity.persist();
		QuarkusTransaction.commit();
	}

	@Test
	void detail_header_scales_with_viewport_instead_of_a_fixed_font_size()
	{
		given().when()
			.get("/ui/movies/" + MOVIE_ID)
			.then()
			.statusCode(200)
			.body(containsString("class=\"detail-title\""))
			.body(containsString("class=\"detail-meta\""))
			.body(not(containsString("font-size:2.2rem")))
			.body(not(containsString("font-size:1.2rem")));
	}
}
