package de.mymiggi.movie.api.entity.config;

public record FrontendConfig(
	String authServerUrl,
	String authClientId,
	String adminRole,
	String platformOwner
)
{
}
