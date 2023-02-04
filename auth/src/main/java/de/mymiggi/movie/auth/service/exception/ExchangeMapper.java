package de.mymiggi.movie.auth.service.exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ws.rs.core.Response;

import io.quarkus.arc.Priority;

@Priority(4000)
public class ExchangeMapper
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
			re = new IOException("Code=" + status);
		}
		return re;
	}

	private String getBody(Response response)
	{
		ByteArrayInputStream is = (ByteArrayInputStream)response.getEntity();
		byte[] bytes = new byte[is.available()];
		int readBytes = is.read(bytes, 0, is.available());
		return new String(bytes);
	}
}
