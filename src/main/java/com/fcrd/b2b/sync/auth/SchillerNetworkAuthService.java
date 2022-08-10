package com.fcrd.b2b.sync.auth;

import java.net.Authenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SchillerNetworkAuthService {
	private static Logger logger = LoggerFactory.getLogger(SchillerNetworkAuthService.class);
	
    @Value("${navision.username}")
    private String username;
	
    @Value("${navision.password}")
    private String password;

	public void setNavisionAuthenticator() {
		try {
			Authenticator.setDefault(new SchillerNetworkAuthenticator(username, password));
		}
		catch (Exception e) {
			logger.error("Error setting Navision network authenticator.", e);
		}
	}
	
	public void setNavisionAuthenticator(String username, String password) {
		try {
			Authenticator.setDefault(new SchillerNetworkAuthenticator(username, password));
		}
		catch (Exception e) {
			logger.error("Error setting Navision network authenticator.", e);
		}
	}

}
