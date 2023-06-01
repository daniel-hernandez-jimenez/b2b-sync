package com.fcrd.b2b.sync.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SyncRequestMessageType {
	@JsonProperty("CHANGE_CUSTOMERS_SYNC_FREQ") CHANGE_CUSTOMERS_SYNC_FREQUENCY,
	@JsonProperty("CHANGE_PRODUCT_SYNC_FREQ") CHANGE_PRODUCT_SYNC_FREQUENCY,
	@JsonProperty("CHANGE_SALES_ORDERS_SYNC_FREQ") CHANGE_SALES_ORDERS_SYNC_FREQUENCY,
	@JsonProperty("CHANGE_SALES_QUOTES_SYNC_FREQ") CHANGE_SALES_QUOTES_SYNC_FREQUENCY
}
