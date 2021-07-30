package com.example.springsecurityinstrumentation.security.listener;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextChangedEvent;
import org.springframework.security.core.context.SecurityContextChangedListener;

import static java.lang.String.format;

/**
 * Helps to handle create/clear/replace/no-context events.
 */
public abstract class AbstractSecurityContextChangedListener implements SecurityContextChangedListener {

	@Override
	public void securityContextChanged(SecurityContextChangedEvent securityContextChangedEvent) {
		SecurityContext previousContext = securityContextChangedEvent.getPreviousContext();
		SecurityContext currentContext = securityContextChangedEvent.getCurrentContext();

		if (previousContext != null) {
			if (currentContext != null) handleReplaceContextEvent(previousContext, currentContext);
			else handleClearContextEvent(previousContext);
		}
		else {
			if (currentContext != null) handleCreateContextEvent(currentContext);
			else handleNoContextEvent();
		}
	}

	abstract void handleCreateContextEvent(SecurityContext context);

	abstract void handleClearContextEvent(SecurityContext context);

	abstract void handleReplaceContextEvent(SecurityContext previousContext, SecurityContext currentContext);

	abstract void handleNoContextEvent();

	protected String getDetailedAuthEventName(Authentication previousAuth, Authentication currentAuth) {
		if (currentAuth != null) {
			if (previousAuth != null) {
				return format("re-auth from:%s to:%s", previousAuth.getName(), currentAuth.getName());
			}
			else {
				return format("auth:%s", currentAuth.getName());
			}
		}
		else {
			if (previousAuth != null) {
				return format("de-auth:%s", previousAuth.getName());
			}
			else {
				return "no-auth";
			}
		}
	}

	protected static String getName(Authentication authentication) {
		return "auth:" + (authentication != null ? authentication.getName() : "null");
	}
}
