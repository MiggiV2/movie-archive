package de.mymiggi.movie.api;

import de.mymiggi.movie.api.actions.admin.UpdateBlocksAction;
import de.mymiggi.movie.api.actions.pub.GetFrontendConfigAction;
import de.mymiggi.movie.api.entity.config.FrontendConfig;
import de.mymiggi.movie.api.service.ExportService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("service")
public class ServiceResource
{
	@Inject
	ExportService exportService;
	@Inject
	GetFrontendConfigAction configAction;
	@Inject
	UpdateBlocksAction updateBlocksAction;

	@GET
	@PermitAll
	@Path("config")
	@Produces(MediaType.APPLICATION_JSON)
	public FrontendConfig getFrontendConfig()
	{
		return configAction.getFrontendConfig();
	}

	@GET
	@Path("export/session")
	@Produces(MediaType.TEXT_PLAIN)
	public String getExportOneTimeSession()
	{
		return exportService.createOneTimeSession();
	}

	@GET
	@PermitAll
	@Path("export/session/{id}/movies.csv")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String exportMoviesInCSV(@PathParam("id") String session)
	{
		return exportService.getMovieCSV(session);
	}

	@POST
	@Path("trigger/block-update")
	@Transactional
	public void updateBlocksInAllMovies()
	{
		updateBlocksAction.updateAllBlocks();
	}
}
