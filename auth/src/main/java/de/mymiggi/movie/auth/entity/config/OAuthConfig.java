package de.mymiggi.movie.auth.entity.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "oauth2")
public interface OAuthConfig
{
	String ClientID();

	String ClientSecret();

	String AdminID();

	String AdminSecret();

	String Schema();

	String Host();

	String Realm();
}
