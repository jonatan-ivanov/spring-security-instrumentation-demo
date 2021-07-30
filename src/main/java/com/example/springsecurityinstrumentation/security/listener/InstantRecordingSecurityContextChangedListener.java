package com.example.springsecurityinstrumentation.security.listener;

import org.springframework.observability.event.Recorder;
import org.springframework.security.core.context.SecurityContext;

import static com.example.springsecurityinstrumentation.observability.event.SecurityContextInstantEvent.REPLACED;
import static com.example.springsecurityinstrumentation.observability.event.SecurityContextInstantEvent.NO_CONTEXT;
import static com.example.springsecurityinstrumentation.observability.event.SecurityContextInstantEvent.CREATED;
import static com.example.springsecurityinstrumentation.observability.event.SecurityContextInstantEvent.CLEARED;

/**
 * Creates instant recordings for SecurityContext changed events.
 * In case of Sleuth, this will mean annotations on the spans.
 */
public class InstantRecordingSecurityContextChangedListener extends AbstractSecurityContextChangedListener {
	private final Recorder<?> recorder;

	public InstantRecordingSecurityContextChangedListener(Recorder<?> recorder) {
		this.recorder = recorder;
	}

	@Override
	void handleCreateContextEvent(SecurityContext context) {
		recorder.recordingFor(CREATED)
				.detailedName(CREATED.getName() + " " + getName(context.getAuthentication()))
				.record();
	}

	@Override
	void handleClearContextEvent(SecurityContext context) {
		recorder.recordingFor(CLEARED)
				.detailedName(CLEARED.getName() + " " + getName(context.getAuthentication()))
				.record();
	}

	@Override
	void handleReplaceContextEvent(SecurityContext previousContext, SecurityContext currentContext) {
		recorder.recordingFor(REPLACED)
				.detailedName(REPLACED.getName() + " " + getDetailedAuthEventName(previousContext.getAuthentication(), currentContext.getAuthentication()))
				.record();
	}

	@Override
	void handleNoContextEvent() {
		recorder.recordingFor(NO_CONTEXT).record();
	}
}
