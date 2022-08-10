package com.fcrd.b2b.sync.messaging;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

public class SyncRequestSessionHandler implements StompSessionHandler {
	private static Logger logger = LoggerFactory.getLogger(SyncRequestSessionHandler.class);
	
	@Override
	public Type getPayloadType(StompHeaders headers) {
		return SyncRequestMessage.class;
	}
	
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		logger.info("Received : " + (SyncRequestMessage) payload);
	}
	
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		logger.info("WebSocket session handler connected!");
	}
	
	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
		String exceptionInfo = "Exception info [session=" + session + ", command=" + command 
				+ ", headers=" + headers + ", payload=" + payload + ", exception=" + exception + "]";
		logger.error(exceptionInfo);
	}
	
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		String transportErrorInfo = "Transport Error info [session=" + session + ", exception=" + exception + "]";
		logger.error(transportErrorInfo);
	}
	
}
