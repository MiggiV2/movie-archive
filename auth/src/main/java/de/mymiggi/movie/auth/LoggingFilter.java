package de.mymiggi.movie.auth;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.vertx.core.http.HttpServerRequest;

@Provider
public class LoggingFilter implements ContainerRequestFilter
{
	private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getSimpleName());

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
		// context.abortWith(Response.status(Response.Status.TOO_MANY_REQUESTS).build());
	}
}