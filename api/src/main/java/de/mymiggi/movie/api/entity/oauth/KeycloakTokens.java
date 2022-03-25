package de.mymiggi.movie.api.entity.oauth;

import com.fasterxml.jackson.annotation.JsonAlias;

public class KeycloakTokens
{
	@JsonAlias("access_token")
	private String accessToken;
	@JsonAlias("expires_in")
	private int expiresIn;
	@JsonAlias("refresh_expires_in")
	private int refreshExpiresIn;
	@JsonAlias("refresh_token")
	private String refreshToken;
	@JsonAlias("token_type")
	private String tokenType;
	@JsonAlias("id_token")
	private String idToken;
	@JsonAlias("not_before_policy")
	private int notBeforePolicy;
	@JsonAlias("season_state")
	private String seasonState;
	@JsonAlias("scope")
	private String scope;

	public String getAccessToken()
	{
		return accessToken;
	}

	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}

	public int getExpiresIn()
	{
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn)
	{
		this.expiresIn = expiresIn;
	}

	public int getRefreshExpiresIn()
	{
		return refreshExpiresIn;
	}

	public void setRefreshExpiresIn(int refreshExpiresIn)
	{
		this.refreshExpiresIn = refreshExpiresIn;
	}

	public String getRefreshToken()
	{
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken)
	{
		this.refreshToken = refreshToken;
	}

	public String getTokenType()
	{
		return tokenType;
	}

	public void setTokenType(String tokenType)
	{
		this.tokenType = tokenType;
	}

	public String getIdToken()
	{
		return idToken;
	}

	public void setIdToken(String idToken)
	{
		this.idToken = idToken;
	}

	public int getNotBeforePolicy()
	{
		return notBeforePolicy;
	}

	public void setNotBeforePolicy(int notBeforePolicy)
	{
		this.notBeforePolicy = notBeforePolicy;
	}

	public String getSeasonState()
	{
		return seasonState;
	}

	public void setSeasonState(String seasonState)
	{
		this.seasonState = seasonState;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope(String scope)
	{
		this.scope = scope;
	}
}
