package de.mymiggi.movie.api.entity.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "oauth2")
public interface OAuthConfig
{
	String RedirectURL();
}
