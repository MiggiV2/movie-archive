package de.mymiggi.movie.auth;

import java.util.HashMap;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.vertx.core.http.HttpServerRequest;

@Provider
public class RequestFilter implements ContainerRequestFilter
{
	private static final Logger LOG = Logger.getLogger(RequestFilter.class.getSimpleName());
	private static final HashMap<String, Integer> ipRequests = new HashMap<String, Integer>();
	private final long lastResetStamp = System.currentTimeMillis();
	@ConfigProperty(name = "max.requests", defaultValue = "20")
	Integer maxRequestsFromIP;
	@Context
	UriInfo info;
	@Context
	HttpServerRequest request;

	@Override
	public void filter(ContainerRequestContext context)
	{
		final String method = context.getMethod();
		final String path = info.getPath();
		final String address = request.getHeader("X-Real-Ip") == null
			? request.remoteAddress().toString()
			: request.getHeader("X-Real-Ip");

		LOG.infof("Request %s %s from IP %s", method, path, address);

		// Can be used to deny requests
		if (System.currentTimeMillis() - lastResetStamp > 1000 * 60)
		{
			ipRequests.clear();
		}
		Integer requestFromIP = ipRequests.get(address);
		requestFromIP = requestFromIP == null ? 1 : requestFromIP + 1;
		if (requestFromIP > maxRequestsFromIP)
		{
			context.abortWith(Response.status(Response.Status.TOO_MANY_REQUESTS).build());
		}
		ipRequests.put(address, requestFromIP);
	}
}