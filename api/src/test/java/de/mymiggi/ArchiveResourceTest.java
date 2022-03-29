package de.mymiggi;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ArchiveResourceTest
{
	protected DefaultPage defaultPage = new DefaultPage()
	{
		@Override
		public int Size()
		{
			return 30;
		}
	};

	@Test
	@Transactional
	void basicTest()
	{
		new TestAction().run(defaultPage);
	}
}