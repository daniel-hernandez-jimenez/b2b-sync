package com.fcrd.b2b.sync.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SchedulingMode {
	@JsonProperty("Rate") RATE,
	@JsonProperty("Delay") DELAY
}
