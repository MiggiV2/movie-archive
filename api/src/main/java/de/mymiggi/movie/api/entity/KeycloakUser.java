package de.mymiggi.movie.api.entity;

import io.quarkus.security.identity.SecurityIdentity;

public class KeycloakUser
{
	private final String userName;

	public KeycloakUser(SecurityIdentity identity)
	{
		this.userName = identity.getPrincipal().getName();
	}

	public String getUserName()
	{
		return userName;
	}
}
