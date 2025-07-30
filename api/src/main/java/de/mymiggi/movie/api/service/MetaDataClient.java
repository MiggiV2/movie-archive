package de.mymiggi.movie.api.service;

import de.mymiggi.movie.api.entity.metadata.MetaDataResponse;
import de.mymiggi.movie.api.entity.metadata.Title;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://api.imdbapi.dev/")
public interface MetaDataClient
{
	@GET
	@Path("advancedSearch/titles")
	MetaDataResponse advancedSearch(@QueryParam("query") String query, @QueryParam("startYear") int startYear, @QueryParam("endYear") int endYear);

	@GET
	@Path("search/titles")
	MetaDataResponse search(@QueryParam("query") String query, @QueryParam("limit") int limit);

	@GET
	@Path("titles/{id}")
	Title getById(@PathParam("id") String id);
}
