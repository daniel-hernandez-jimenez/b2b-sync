package com.fcrd.b2b.sync;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fcrd.b2b.sync.auth.SchillerNetworkAuthService;

@SpringBootApplication
@EnableScheduling
public class SyncApplication {
	private static Logger logger = LoggerFactory.getLogger(SyncApplication.class);
    
    @Autowired
    SchillerNetworkAuthService schillerNetworkAuthService;
	
	public static void main(String[] args) {
		SpringApplication.run(SyncApplication.class, args);
	}
	
	@PostConstruct
	private void setNavisionAuthenticator() {
		logger.info("Set Navision network authenticator");
		schillerNetworkAuthService.setNavisionAuthenticator();
	}
	
}
