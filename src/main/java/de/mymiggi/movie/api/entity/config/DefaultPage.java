package de.mymiggi.movie.api.entity.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "de.mymiggi.movie.default-page")
public interface DefaultPage
{
	int Size();
}
