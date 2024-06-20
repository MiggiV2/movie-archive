package de.mymiggi.movie.api.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class MovieEntity extends PanacheEntityBase
{
	public int year;
	public String name;
	public String uuid;
	public String block;
	public String wikiUrl;
	public String type;
	public String originalName;

	@Id
	@SequenceGenerator(name = "movieSequence", sequenceName = "movie_id_seq", initialValue = 364)
	@GeneratedValue(generator = "movieSequence")
	public Long id;

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
			return new ArrayList<>();
		}
	}

	@Override
	public String toString()
	{
		return String.format("year:%d,name:%s,uuid:%s,block:%s,wikiUrl:%s,type:%s", year, name, uuid, block, wikiUrl, type);
	}
}
