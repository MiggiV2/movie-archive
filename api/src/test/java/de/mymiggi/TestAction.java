package de.mymiggi;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;

import de.mymiggi.movie.api.actions.admin.AddMovieAction;
import de.mymiggi.movie.api.actions.admin.DeleteMovieAction;
import de.mymiggi.movie.api.actions.admin.UpdateMovieAction;
import de.mymiggi.movie.api.actions.user.GetMovieByIDAction;
import de.mymiggi.movie.api.actions.user.GetSortedMoviesAction;
import de.mymiggi.movie.api.actions.user.SearchAction;
import de.mymiggi.movie.api.entity.KeycloakUser;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;

public class TestAction
{
	@SuppressWarnings("unchecked")
	/**
	 * Needs a database which contains dummy data
	 */
	public void run(DefaultPage defaultPage)
	{
		MovieEntity movieEntity = new MovieEntity(2077, "Cyberpunk-Movie", "Block 77", "", "DB");

		/** Add-Test **/
		Response response = new AddMovieAction().run(movieEntity, new KeycloakUser());
		movieEntity = response.readEntity(MovieEntity.class);
		Assertions.assertEquals(response.getStatus(), 200);

		/** Update-Test **/
		movieEntity.name = "Cyberpunk";
		response = new UpdateMovieAction().run(movieEntity, new KeycloakUser());
		movieEntity = response.readEntity(MovieEntity.class);
		Assertions.assertEquals(response.getStatus(), 200);

		/** GetByID-Test **/
		response = new GetMovieByIDAction().run(movieEntity.id);
		movieEntity = response.readEntity(MovieEntity.class);
		Assertions.assertEquals(response.getStatus(), 200);

		/** Sort-Test-1 **/
		response = new GetSortedMoviesAction().runByYear(0, true, defaultPage);
		List<MovieEntity> movies = response.readEntity(new ArrayList<MovieEntity>().getClass());
		Assertions.assertEquals(movies.get(0).year, movieEntity.year);

		/** Sort-Test-2 **/
		response = new GetSortedMoviesAction().runByYear(0, false, defaultPage);
		movies = response.readEntity(new ArrayList<MovieEntity>().getClass());
		Assertions.assertNotEquals(movies.get(0).year, movieEntity.year);

		/** Search-Test-1 **/
		response = new SearchAction().run("terminator");
		movies = response.readEntity(new ArrayList<MovieEntity>().getClass());
		Assertions.assertEquals(response.getStatus(), 200);
		Assertions.assertFalse(movies.isEmpty());
		movies.forEach(m -> System.out.println(m.name));

		/** Search-Test-2 **/
		response = new SearchAction().run("not-a-movie-lol");
		movies = response.readEntity(new ArrayList<MovieEntity>().getClass());
		Assertions.assertEquals(response.getStatus(), 204);
		Assertions.assertEquals(movies, null);

		/** Delete-Test **/
		response = new DeleteMovieAction().run(movieEntity.id, new KeycloakUser());
		Assertions.assertEquals(response.getStatus(), 204);
	}
}
