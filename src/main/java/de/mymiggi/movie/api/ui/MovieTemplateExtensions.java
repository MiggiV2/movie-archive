package de.mymiggi.movie.api.ui;

import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.MoviePreview;
import io.quarkus.qute.TemplateExtension;

/**
 * Qute template extensions for movie view models, e.g. {@code {movie.runtimeMinutes}} or
 * {@code {movie.posterThumb}}.
 */
@TemplateExtension
public class MovieTemplateExtensions
{
	/** Fallback poster shipped with the UI, shown when a movie has no image. */
	private static final String DEFAULT_POSTER = "/img/default-poster.webp";

	/** Runtime is stored in seconds; render it in whole minutes. */
	public static int runtimeMinutes(DetailedMovie movie)
	{
		return movie.getRuntime() / 60;
	}

	/** Grid thumbnail: a smaller, cropped IMDb variant (matches the Vue SPA), or the fallback. */
	public static String posterThumb(MoviePreview movie)
	{
		return thumb(movie.getImage());
	}

	/**
	 * Detail-view poster. The poster renders at most 320px wide, so we serve the same low-res
	 * 380x562 IMDb variant as the grid thumbnail instead of the multi-MB original.
	 */
	public static String posterFull(DetailedMovie movie)
	{
		return thumb(movie.getImage());
	}

	private static String thumb(String image)
	{
		if (image == null || image.isBlank())
		{
			return DEFAULT_POSTER;
		}
		// IMDb image URLs accept inline resize/crop directives; request a 380x562 poster.
		return image.replace("@._V1_.jpg", "@._V1_QL75_UX380_CR0,0,380,562_.jpg");
	}
}
