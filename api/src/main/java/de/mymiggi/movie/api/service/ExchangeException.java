package de.mymiggi.movie.api.service;

public class ExchangeException extends Exception
{
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
