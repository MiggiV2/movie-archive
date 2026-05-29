package de.mymiggi.movie.api.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.mymiggi.movie.api.entity.DetailedMovie;
import de.mymiggi.movie.api.entity.MoviePreview;

import org.junit.jupiter.api.Test;

class MovieTemplateExtensionsTest
{
	private static final String RAW_IMAGE = "https://m.media-amazon.com/images/M/abc@._V1_.jpg";
	private static final String SIZED_IMAGE = "https://m.media-amazon.com/images/M/abc@._V1_QL75_UX380_CR0,0,380,562_.jpg";
	private static final String DEFAULT_POSTER = "/img/default-poster.webp";

	@Test
	void posterThumbResizesImage()
	{
		MoviePreview preview = new MoviePreview();
		preview.setImage(RAW_IMAGE);

		assertEquals(SIZED_IMAGE, MovieTemplateExtensions.posterThumb(preview));
	}

	@Test
	void posterFullUsesLowResVariantLikePreview()
	{
		DetailedMovie movie = new DetailedMovie();
		movie.setImage(RAW_IMAGE);

		assertEquals(SIZED_IMAGE, MovieTemplateExtensions.posterFull(movie));
	}

	@Test
	void posterFullFallsBackWhenImageMissing()
	{
		DetailedMovie movie = new DetailedMovie();
		movie.setImage(null);

		assertEquals(DEFAULT_POSTER, MovieTemplateExtensions.posterFull(movie));
	}
}
