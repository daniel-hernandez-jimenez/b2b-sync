package com.fcrd.b2b.sync.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Service
public class SyncRequestMessageService {
	private static Logger logger = LoggerFactory.getLogger(SyncRequestMessageService.class);
	
    @Value("${messaging.websocket.url}")
    private String webSocketUrl;
    
    @Value("${messaging.websocket.endpoint}")
    private String webSocketEndpoint;
	
    @Value("${messaging.websocket.topic}")
    private String webSocketTopic;
	
	@Scheduled(initialDelayString="${messaging.service.initialDelay:10}000", fixedDelay=Long.MAX_VALUE)
	public void startConnection() {
		try {
			logger.info("Starting connection with messaging websocket endpoint: " + webSocketUrl + webSocketEndpoint);
			WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
			stompClient.setMessageConverter(new MappingJackson2MessageConverter());
			
			StompSessionHandler sessionHandler = new SyncRequestSessionHandler();
			StompSession stompSession = stompClient.connect(webSocketUrl+webSocketEndpoint, sessionHandler).get();
			stompSession.subscribe(webSocketTopic, sessionHandler);
			
			logger.info("WebSocket session handler subscribed to " + webSocketTopic);
		}
		catch (Exception e) {
//			logger.error("Error connecting to the WebSocket", e);
			logger.error("Error connecting to the WebSocket. " + e.getMessage());
		}
	}
}
