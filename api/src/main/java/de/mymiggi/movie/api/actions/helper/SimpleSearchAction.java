package de.mymiggi.movie.api.actions.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.mymiggi.movie.api.entity.db.MovieEntity;

public class SimpleSearchAction
{
	private List<MovieEntity> nameEquals = new ArrayList<MovieEntity>();
	private List<MovieEntity> nameContains = new ArrayList<MovieEntity>();
	private Map<Integer, List<MovieEntity>> wordsContains = new TreeMap<Integer, List<MovieEntity>>(Collections.reverseOrder());

	public List<MovieEntity> run(String query)
	{
		List<MovieEntity> result = new ArrayList<MovieEntity>();
		List<MovieEntity> db = MovieEntity.listAll();
		String[] words = query.split(" ");
		db.forEach(m -> checkForQuery(query.toLowerCase(), words, m));
		if (!nameEquals.isEmpty())
		{
			result.addAll(nameEquals);
		}
		if (!nameContains.isEmpty() && result.size() < 30)
		{
			result.addAll(nameContains);
		}
		if (!wordsContains.isEmpty() && result.size() < 30)
		{
			result.addAll(buildContaisList());
		}
		return result;
	}

	private List<MovieEntity> buildContaisList()
	{
		List<MovieEntity> words = new ArrayList<MovieEntity>();
		wordsContains.forEach((index, list) -> words.addAll(list));
		return words;
	}

	private void checkForQuery(String query, String[] words, MovieEntity m)
	{
		String name = m.name.toLowerCase();
		if (name.equals(query))
		{
			nameEquals.add(m);
		}
		else if (name.contains(query))
		{
			nameContains.add(m);
		}
		else
		{
			handleWords(words, m, name);
		}
	}

	private void handleWords(String[] words, MovieEntity m, String name)
	{
		int hits = 0;
		for (String w : words)
		{
			if (name.contains(w))
			{
				hits++;
			}
		}
		if (hits > 0)
		{
			if (wordsContains.containsKey(hits))
			{
				List<MovieEntity> wordList = wordsContains.get(hits);
				wordList.add(m);
				wordsContains.put(hits, wordList);
			}
			else
			{
				List<MovieEntity> wordList = new ArrayList<MovieEntity>();
				wordList.add(m);
				wordsContains.put(hits, wordList);
			}
		}
	}
}
