package de.mymiggi.movie.api.entity.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class TagEntity extends PanacheEntity
{
	private String name;
	@JsonIgnore
	private LocalDateTime created;

	public TagEntity()
	{
	}

	public TagEntity(String name, LocalDateTime created)
	{
		this.name = name;
		this.created = created;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public LocalDateTime getCreated()
	{
		return created;
	}

	public void setCreated(LocalDateTime created)
	{
		this.created = created;
	}
}
