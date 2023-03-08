package com.fcrd.b2b.sync.service.b2b;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fcrd.b2b.api.client.core.api.SessionsApi;
import com.fcrd.b2b.api.client.core.model.CreateSessionRequest;
import com.fcrd.b2b.api.client.core.model.CreateSessionResponse;
import com.fcrd.b2b.api.client.sync.ApiClient;
import com.fcrd.b2b.api.client.sync.ApiException;
import com.fcrd.b2b.api.client.sync.auth.HttpBearerAuth;
import com.fcrd.b2b.api.client.sync.model.SyncStats;
import com.fcrd.b2b.sync.service.B2BUnauthorizedException;
import com.fcrd.b2b.sync.utils.DateTimeUtils;

public class B2BBaseService {
	private static Logger logger = LoggerFactory.getLogger(B2BBaseService.class);
    
    @Value("${nav.datetime.pattern}")
    protected String navDateTimePattern;
	
	@Value("${b2b.core.api.base.path}")
	private String b2bAPICoreBasePath;
	
	@Value("${b2b.sync.api.base.path}")
	private String b2bAPISyncBasePath;
	
	@Value("${b2b.api.client.readTimeout}")
	private Integer b2bAPIClientReadTimeout;
	
	@Value("${b2b.username}")
	private String username;
    
    @Value("${b2b.password}")
    private String password;
    
    @Value("${b2b.token}")
    private String b2bToken;
	
	private static com.fcrd.b2b.api.client.core.ApiClient apiCoreClient;
	private static SessionsApi sessionsApi;
	
	private static com.fcrd.b2b.api.client.sync.ApiClient apiSyncClient;
	
	private static HttpBearerAuth b2bAuth;
	
	@PostConstruct
	private void initialSettings() {
		if (apiCoreClient==null) {
			apiCoreClient = com.fcrd.b2b.api.client.core.Configuration.getDefaultApiClient();
			apiCoreClient.setBasePath(b2bAPICoreBasePath);
			sessionsApi = new SessionsApi(apiCoreClient);
		}
		
		if (apiSyncClient==null) {
			apiSyncClient = com.fcrd.b2b.api.client.sync.Configuration.getDefaultApiClient();
			apiSyncClient.setBasePath(b2bAPISyncBasePath);
			apiSyncClient.setReadTimeout(b2bAPIClientReadTimeout);
			
			b2bAuth = (HttpBearerAuth) apiSyncClient.getAuthentication("b2bAuth");
		}
		
		// for tests in development 
		if (b2bToken != null) b2bAuth.setBearerToken(b2bToken);
	}
	
	protected static ApiClient getApiSyncClient() {
		return apiSyncClient;
	}

	public void createAPISession() {
		logger.info("Creating a new B2B session");
		
		b2bToken = null;
		try {
			CreateSessionRequest createSessionRequest = new CreateSessionRequest();
			createSessionRequest.setUsername(username);
			createSessionRequest.setPassword(password);
			
			CreateSessionResponse createSessionResponse = sessionsApi.createSession(createSessionRequest);
			
			b2bToken = createSessionResponse.getToken();
		}
		catch (Exception e) {
			logger.error("An error ocurred while trying to create a new B2B session.", e);
		}
		finally {
			b2bAuth.setBearerToken(b2bToken);
		}
	}
	
	public String getLastExternalModifiedDateTime() throws Exception {
		String lastModifiedDateTime = null;
		try {
			SyncStats syncStats = requestSyncStats();
			
			if (syncStats.getLastExternalModifiedTime() != null) {
				lastModifiedDateTime = DateTimeUtils.offsetDateTimeToString(syncStats.getLastExternalModifiedTime(), navDateTimePattern);
			}
			
			logger.info("LastExternalModifiedDateTime: " + lastModifiedDateTime);
		}
		catch (ApiException e) {
			throw (e.getCode()==401 ? new B2BUnauthorizedException("Unauthorized") : e);
		}
		return lastModifiedDateTime;
	}
	
	protected SyncStats requestSyncStats() throws Exception {
		throw new Exception("requestSyncStats is not implemented");
	}
	
}
