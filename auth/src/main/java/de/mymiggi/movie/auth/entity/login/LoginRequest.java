package de.mymiggi.movie.auth.entity.login;

import javax.ws.rs.FormParam;

public class LoginRequest
{
	@FormParam("grant_type")
	private String grandType;

	@FormParam("client_id")
	private String clientID;

	@FormParam("client_secret")
	private String clientSecret;

	@FormParam("username")
	private String username;

	@FormParam("password")
	private String password;

	@FormParam("refresh_token")
	private String refreshToken;

	public String getRefreshToken()
	{
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken)
	{
		this.refreshToken = refreshToken;
	}

	public String getGrandType()
	{
		return grandType;
	}

	public void setGrandType(String grandType)
	{
		this.grandType = grandType;
	}

	public String getClientID()
	{
		return clientID;
	}

	public void setClientID(String clientID)
	{
		this.clientID = clientID;
	}

	public String getClientSecret()
	{
		return clientSecret;
	}

	public void setClientSecret(String clientSecret)
	{
		this.clientSecret = clientSecret;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
}
