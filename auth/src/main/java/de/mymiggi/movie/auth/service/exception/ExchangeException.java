package de.mymiggi.movie.auth.service.exception;

import java.io.Serial;

public class ExchangeException extends Exception
{
	@Serial
	private static final long serialVersionUID = 4151468759002665112L;

	public ExchangeException(String msg)
	{
		super(msg);
	}

	@Override
	public String toString()
	{
		return getMessage();
	}
}
