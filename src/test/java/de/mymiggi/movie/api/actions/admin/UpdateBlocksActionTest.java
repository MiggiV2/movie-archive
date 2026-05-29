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
		int index = 7;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("A2", block);
	}

	@Test
	void shouldGetBlockA1()
	{
		// given
		int index = 3;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("A1", block);
	}

	@Test
	void shouldGetBlockA4()
	{
		// given
		int index = 3 * 4 + 1;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("A4", block);
	}

	@Test
	void shouldGetBlockB1()
	{
		// given
		int index = 4 * 4;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("B1", block);
	}

	@Test
	void shouldGetBlockB4()
	{
		// given
		int index = 4 * 4 + 4 * 5 - 1;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("B4", block);
	}

	@Test
	void shouldGetBlockC2()
	{
		// given
		int index = 4 * 4 + 4 * 5 + 7;

		// when
		String block = action.getNewBlock(index);

		// then
		assertEquals("C2", block);
	}
}