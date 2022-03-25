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

	@FormParam("code")
	private String code;

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
}
