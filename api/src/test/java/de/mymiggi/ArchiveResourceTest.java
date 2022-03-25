package de.mymiggi;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;

import de.mymiggi.movie.api.entity.config.DefaultPage;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ArchiveResourceTest
{
	@Inject
	protected DefaultPage defaultPage;

	@Test
	@Transactional
	void basicTest()
	{
		new TestAction().run(defaultPage);
	}
}