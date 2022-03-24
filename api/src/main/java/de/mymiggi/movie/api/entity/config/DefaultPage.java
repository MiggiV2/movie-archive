package de.mymiggi.movie.api.entity.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "default-page")
public interface DefaultPage
{
	int Size();
}
