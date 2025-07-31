package de.mymiggi.movie.api;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.service.ExportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("movie-archive/public")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicResource
{
	DefaultPage defaultPage;
	ExportService exportService;

	@Inject
	public PublicResource(DefaultPage defaultPage, ExportService exportService)
	{
		this.defaultPage = defaultPage;
		this.exportService = exportService;
	}

	@GET
	@Path("movie-count")
	@Produces(MediaType.TEXT_PLAIN)
	public long getMovieCount()
	{
		return MovieEntity.count();
	}

	@GET
	@Path("movie-page-count")
	@Produces(MediaType.TEXT_PLAIN)
	public long getMoviePageCount()
	{
		return Math.ceilDiv(MovieEntity.count(), defaultPage.Size()) - 1;
	}

	@GET
	@Path("export/csv/{session}/export-movies.csv")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String exportMoviesInCSV(@PathParam("session") String session)
	{
		return exportService.getMovieCSV(session);
	}
}
