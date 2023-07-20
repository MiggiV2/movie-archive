package de.mymiggi.movie.api.service;

import java.io.ByteArrayInputStream;
import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Priority(4000)
public class ExchangeMapper implements ResponseExceptionMapper<Exception>
{
	public Exception toThrowable(Response response)
	{
		int status = response.getStatus();

		String msg = getBody(response);

		Exception re;
		switch (status)
		{
			case 400:
				re = new ExchangeException(msg);
				break;
			default:
				re = new WebApplicationException(status);
		}
		return re;
	}

	private String getBody(Response response)
	{
		ByteArrayInputStream is = (ByteArrayInputStream)response.getEntity();
		byte[] bytes = new byte[is.available()];
		is.read(bytes, 0, is.available());
		String body = new String(bytes);
		return body;
	}
}