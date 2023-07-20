package de.mymiggi.movie.api;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.vertx.core.http.HttpServerRequest;

import java.util.Objects;

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
		final String address = Objects.isNull(request.getHeader("X-Real-Ip"))
			? request.remoteAddress().toString()
			: request.getHeader("X-Real-Ip");

		LOG.infof("Request %s %s from IP %s", method, path, address);
	}
}
