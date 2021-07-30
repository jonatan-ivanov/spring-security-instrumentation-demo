package com.example.springsecurityinstrumentation.observability.event;

import org.springframework.observability.event.instant.InstantEvent;

public enum SecurityContextInstantEvent implements InstantEvent {
	CREATED("security.context.created", ""),
	CLEARED("security.context.cleared", ""),
	REPLACED("security.context.replaced", ""),
	NO_CONTEXT("security.context.not-present", "");

	private final String name;
	private final String description;

	SecurityContextInstantEvent(String name, String description) {
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
