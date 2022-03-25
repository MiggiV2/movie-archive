package de.mymiggi;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeArchiveResouceTest extends ArchiveResourceTest
{
	protected DefaultPage defaultPage = new DefaultPage()
	{
		@Override
		public int Size()
		{
			return 30;
		}
	};
}