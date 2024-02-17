package de.mymiggi.movie.api.service;

import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import java.io.ByteArrayInputStream;

@Priority(4000)
public class ExchangeMapper implements ResponseExceptionMapper<Exception>
{
	public Exception toThrowable(Response response)
	{
		int status = response.getStatus();

		String msg = getBody(response);

		Exception re;
		if (status == 400)
		{
			re = new ExchangeException(msg);
		}
		else
		{
			re = new WebApplicationException(status);
		}
		return re;
	}

	private String getBody(Response response)
	{
		ByteArrayInputStream is = (ByteArrayInputStream)response.getEntity();
		byte[] bytes = new byte[is.available()];
		is.read(bytes, 0, is.available());
		return new String(bytes);
	}
}