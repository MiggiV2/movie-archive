package de.mymiggi.movie.api.entity;

public enum MovieType
{
	DVD
	{
		@Override
		public String toString()
		{
			return "DVD";
		}
	},
	BLUERAY_DISK
	{
		@Override
		public String toString()
		{
			return "DB";
		}
	},
	FOURK_BLUERAY
	{
		@Override
		public String toString()
		{
			return "4k-DB";
		}
	};
}
