package de.mymiggi.movie.api.ui;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("ui")
public class HomePageResource
{
	@Inject
	UiSession session;

	@ConfigProperty(name = "de.mymiggi.movie.owner")
	String owner;

	@CheckedTemplate
	public static class Templates
	{
		public static native TemplateInstance home(long movieCount, String owner, boolean isAdmin, String username);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance home()
	{
		return Templates.home(MovieEntity.count(), owner, session.isAdmin(), session.getUsername());
	}
}
