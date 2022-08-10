package com.fcrd.b2b.sync.auth;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class SchillerNetworkAuthenticator extends Authenticator {
	
	private final String username;
	private final char[] password;
	
	public SchillerNetworkAuthenticator(final String username, final String password) {
		super();
		this.username = new String(username);
		this.password = password.toCharArray();
	}
	
	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return (new PasswordAuthentication (username, password));
	}
	
}
