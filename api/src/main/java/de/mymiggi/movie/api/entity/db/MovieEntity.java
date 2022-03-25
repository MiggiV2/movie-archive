package de.mymiggi.movie.api.entity.db;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class MovieEntity extends PanacheEntity
{
	public int year;
	public String name;
	public String uuid;
	public String block;
	public String wikiUrl;
	public String type;

	public MovieEntity()
	{
	}

	public MovieEntity(int year, String name, String block, String wikiUrl, String type)
	{
		this.year = year;
		this.name = name;
		this.block = block;
		this.wikiUrl = wikiUrl;
		this.type = type;
	}

	public static List<MovieEntity> startWith(String start)
	{
		try (Stream<MovieEntity> persons = MovieEntity.streamAll())
		{
			return persons
				.filter(n -> n.name.startsWith(start))
				.collect(Collectors.toList());
		}
		catch (Exception e)
		{
			return new ArrayList<MovieEntity>();
		}
	}
}
