package com.fcrd.b2b.sync.service.b2b;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fcrd.b2b.core.api.client.SessionsApi;
import com.fcrd.b2b.core.api.client.model.CreateSessionRequest;
import com.fcrd.b2b.core.api.client.model.CreateSessionResponse;
import com.fcrd.b2b.sync.api.ApiClient;
import com.fcrd.b2b.sync.api.ApiException;
import com.fcrd.b2b.sync.api.auth.HttpBearerAuth;
import com.fcrd.b2b.sync.api.client.model.SyncStats;
import com.fcrd.b2b.sync.service.B2BUnauthorizedException;
import com.fcrd.b2b.sync.utils.DateTimeUtils;

public class B2BBaseService {
	private static Logger logger = LoggerFactory.getLogger(B2BBaseService.class);
	
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
	
	private static com.fcrd.b2b.core.api.ApiClient apiCoreClient;
	private static SessionsApi sessionsApi;
	
	private static com.fcrd.b2b.sync.api.ApiClient apiSyncClient;
	
	private static String b2bToken;
	private static HttpBearerAuth b2bAuth;
	
	@PostConstruct
	private void initialSettings() {
		logger.info("B2BBaseService.initialSettings");
		
		if (apiCoreClient==null) {
			apiCoreClient = com.fcrd.b2b.core.api.Configuration.getDefaultApiClient();
			apiCoreClient.setBasePath(b2bAPICoreBasePath);
			sessionsApi = new SessionsApi(apiCoreClient);
		}
		
		if (apiSyncClient==null) {
			apiSyncClient = com.fcrd.b2b.sync.api.Configuration.getDefaultApiClient();
			apiSyncClient.setBasePath(b2bAPISyncBasePath);
			apiSyncClient.setReadTimeout(b2bAPIClientReadTimeout);
			
			b2bAuth = (HttpBearerAuth) apiSyncClient.getAuthentication("b2bAuth");
		}
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
//				lastModifiedDateTime = DateTimeUtils.offsetDateTimeToString(syncStats.getLastExternalModifiedTime(), dateTimePattern);
				lastModifiedDateTime = DateTimeUtils.offsetDateTimeToString(syncStats.getLastExternalModifiedTime(), "MM/dd/yyyy HH:mm:ss.SSS");
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
