package com.fcrd.b2b.sync.messaging;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

public class SyncRequestMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private SyncRequestMessageType type;
	private Date date;
	private String message;
	private JsonNode request;

	public SyncRequestMessage() {
		super();
	}

	public SyncRequestMessageType getType() {
		return type;
	}
	public void setType(SyncRequestMessageType type) {
		this.type = type;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public JsonNode getRequest() {
		return request;
	}
	public void setRequest(JsonNode request) {
		this.request = request;
	}
	
	@Override
	public String toString() {
		return "SyncRequestMessage [type=" + type + ", date=" + date + ", message=" + message + ", request=" + request
				+ "]";
	}
	
}
