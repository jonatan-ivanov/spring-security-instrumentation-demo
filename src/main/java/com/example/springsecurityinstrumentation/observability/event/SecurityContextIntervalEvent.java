package com.example.springsecurityinstrumentation.observability.event;

import org.springframework.observability.event.interval.IntervalEvent;

public enum SecurityContextIntervalEvent implements IntervalEvent {
	SECURITY_CONTEXT("security.context","");

	private final String name;
	private final String description;

	SecurityContextIntervalEvent(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}
}
