package de.mymiggi.movie.api.entity;

public enum AuditLogType
{
	DELETE
	{
		@Override
		public String toString()
		{
			return "delete";
		}
	},
	UPDATE
	{
		@Override
		public String toString()
		{
			return "update";
		}
	},
	ADD
	{
		@Override
		public String toString()
		{
			return "add";
		}
	},
	TOKEN_CREATE
	{
		@Override
		public String toString()
		{
			return "token-create";
		}
	},
	TOKEN_REVOKE
	{
		@Override
		public String toString()
		{
			return "token-revoke";
		}
	}
}
