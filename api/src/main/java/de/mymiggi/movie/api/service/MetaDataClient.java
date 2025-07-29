package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.metadata.MedaDataResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://api.imdbapi.dev/")
public interface MetaDataClient
{
	@GET
	@Path("advancedSearch/titles")
	MedaDataResponse advancedSearch(@QueryParam("query") String query, @QueryParam("startYear") int startYear, @QueryParam("endYear") int endYear);

	@GET
	@Path("search/titles")
	MedaDataResponse search(@QueryParam("query") String query, @QueryParam("limit") int limit);
}
