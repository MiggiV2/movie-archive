package de.mymiggi.movie.api.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ShortMessage
{
	private String content;
	private MessageStatus status;

	public ShortMessage(String content, MessageStatus status)
	{
		this.content = content;
		this.status = status;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public MessageStatus getStatus()
	{
		return status;
	}

	public void setStatus(MessageStatus status)
	{
		this.status = status;
	}
}
