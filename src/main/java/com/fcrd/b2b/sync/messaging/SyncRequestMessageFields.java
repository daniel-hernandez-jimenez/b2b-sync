package com.fcrd.b2b.sync.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SyncRequestMessageFields {
	@JsonProperty("customersSyncFixedRateString") CUSTOMERS_SYNC_FIXRATE_STRING,
	@JsonProperty("productsSyncFixedRateString") PRODUCT_SYNC_FIXRATE_STRING,
	@JsonProperty("salesOrdersSyncFixedRateString") SALES_ORDERS_SYNC_FIXRATE_STRING,
	@JsonProperty("salesQuotesSyncFixedRateString") SALES_QUOTES_SYNC_FIXRATE_STRING
}
