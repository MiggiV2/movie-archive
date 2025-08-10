package de.mymiggi.movie.api.actions.pub;

import de.mymiggi.movie.api.entity.config.FrontendConfig;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GetFrontendConfigAction
{
	@ConfigProperty(name = "quarkus.oidc.auth-server-url")
	String authServerUrl;

	@ConfigProperty(name = "quarkus.oidc.client-id")
	String authClientId;

	@ConfigProperty(name = "quarkus.http.auth.policy.admin-policy.roles-allowed")
	String adminRole;

	@ConfigProperty(name = "de.mymiggi.movie.owner")
	String platformOwner;

	public FrontendConfig getFrontendConfig()
	{
		return new FrontendConfig(
			authServerUrl,
			authClientId,
			adminRole,
			platformOwner
		);
	}
}
