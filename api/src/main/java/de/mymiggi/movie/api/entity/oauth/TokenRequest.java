package de.mymiggi.movie.api.entity.oauth;

import javax.ws.rs.FormParam;

public class TokenRequest
{
	@FormParam("grant_type")
	private String grandType;

	@FormParam("client_id")
	private String clientID;

	@FormParam("client_secret")
	private String clientSecret;

	@FormParam("redirect_uri")
	private String redircetURL;

	@FormParam("username")
	private String username;

	@FormParam("password")
	private String password;

	@FormParam("code")
	private String code;

	@FormParam("refresh_token")
	private String refreshToken;

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

	public String getRedircetURL()
	{
		return redircetURL;
	}

	public void setRedircetURL(String redircetURL)
	{
		this.redircetURL = redircetURL;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getRefreshToken()
	{
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken)
	{
		this.refreshToken = refreshToken;
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
