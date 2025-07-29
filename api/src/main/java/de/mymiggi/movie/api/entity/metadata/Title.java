package de.mymiggi.movie.api.entity.metadata;

import java.util.ArrayList;
import java.util.List;

public class Title
{
	public String id;
	public String type;
	public boolean isAdult;
	public String primaryTitle;
	public String originalTitle;
	public PrimaryImage primaryImage;
	public int startYear;
	public int endYear;
	public int runtimeSeconds;
	public List<String> genres;
	public Rating rating;
	public Metacritic metacritic;
	public String plot;
	public ArrayList<OriginCountry> originCountries;
	public ArrayList<SpokenLanguage> spokenLanguages;
}
