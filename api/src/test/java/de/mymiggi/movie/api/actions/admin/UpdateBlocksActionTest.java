package de.mymiggi.movie.api.actions.admin;

import de.mymiggi.movie.api.entity.db.MovieEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class UpdateBlocksActionTest
{
	@Inject
	UpdateBlocksAction action;

	@Test
	void shouldUpdateAllBlocks()
	{
		// given
		long count = MovieEntity.count();

		// when
		long updated = action.updateAllBlocks();

		// then
		assertEquals(count, updated);
	}

	@Test
	void shouldGetBlockA2()
	{
		// given
		int index = 42;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("A2", block);
	}

	@Test
	void shouldGetBlockA1()
	{
		// given
		int index = 12;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("A1", block);
	}

	@Test
	void shouldGetBlockA4()
	{
		// given
		int index = 3 * 30 + 10;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("A4", block);
	}

	@Test
	void shouldGetBlockB1()
	{
		// given
		int index = 4 * 30;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("B1", block);
	}

	@Test
	void shouldGetBlockB4()
	{
		// given
		int index = 4 * 30 + 4 * 40 - 1;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("B4", block);
	}

	@Test
	void shouldGetBlockC2()
	{
		// given
		int index = 4 * 30 + 4 * 40 + 30;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("C2", block);
	}
}