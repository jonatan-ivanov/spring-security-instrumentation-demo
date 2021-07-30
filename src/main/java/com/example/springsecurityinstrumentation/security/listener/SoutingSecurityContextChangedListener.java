package com.example.springsecurityinstrumentation.security.listener;

import java.time.Instant;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextChangedEvent;
import org.springframework.security.core.context.SecurityContextChangedListener;

import static java.lang.System.identityHashCode;

/**
 * Prints SecurityContext changed events to std out.
 */
public class SoutingSecurityContextChangedListener implements SecurityContextChangedListener {
	@Override
	public void securityContextChanged(SecurityContextChangedEvent securityContextChangedEvent) {
		SecurityContext previousContext = securityContextChangedEvent.getPreviousContext();
		SecurityContext currentContext = securityContextChangedEvent.getCurrentContext();

		System.out.println();
		if (currentContext != null) {
			if (previousContext != null) System.out.println(Instant.now() + " REPLACE");
			else System.out.println(Instant.now() + " START");
		}
		else {
			if (previousContext != null) System.out.println(Instant.now() + " STOP");
			else System.out.println(Instant.now() + " NO_AUTH");
		}

		System.out.printf("prev: %d ctx: %s%n", identityHashCode(previousContext), previousContext);
		System.out.printf("curr: %d ctx: %s%n", identityHashCode(currentContext), currentContext);
	}
}
