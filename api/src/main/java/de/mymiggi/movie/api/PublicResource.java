package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.pub.GetFrontendConfigAction;
import de.mymiggi.movie.api.entity.config.DefaultPage;
import de.mymiggi.movie.api.entity.config.FrontendConfig;
import de.mymiggi.movie.api.entity.db.MovieEntity;
import de.mymiggi.movie.api.service.ExportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("public")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Deprecated
public class PublicResource
{
	DefaultPage defaultPage;
	ExportService exportService;
	GetFrontendConfigAction configAction;

	@Inject
	public PublicResource(DefaultPage defaultPage, ExportService exportService, GetFrontendConfigAction configAction)
	{
		this.defaultPage = defaultPage;
		this.exportService = exportService;
		this.configAction = configAction;
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
	@Path("config")
	@Produces(MediaType.APPLICATION_JSON)
	public FrontendConfig getFrontendConfig()
	{
		return configAction.getFrontendConfig();
	}

	@GET
	@Path("export/csv/{session}/export-movies.csv")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String exportMoviesInCSV(@PathParam("session") String session)
	{
		return exportService.getMovieCSV(session);
	}
}
