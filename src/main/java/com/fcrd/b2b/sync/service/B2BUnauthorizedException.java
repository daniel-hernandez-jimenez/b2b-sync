package com.fcrd.b2b.sync.service;

public class B2BUnauthorizedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public B2BUnauthorizedException(String errorMessage) {
		super(errorMessage);
	}
}
