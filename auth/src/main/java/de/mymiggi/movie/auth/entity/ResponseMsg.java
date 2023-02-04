package de.mymiggi.movie.auth.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ResponseMsg
{
	private String content;
	private MsgType type;

	public ResponseMsg()
	{

	}

	public ResponseMsg(String content, MsgType type)
	{
		this.content = content;
		this.type = type;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public MsgType getType()
	{
		return type;
	}

	public void setType(MsgType type)
	{
		this.type = type;
	}
}
