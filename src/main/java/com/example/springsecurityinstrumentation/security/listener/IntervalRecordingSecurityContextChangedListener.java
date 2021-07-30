package com.example.springsecurityinstrumentation.security.listener;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.observability.event.Recorder;
import org.springframework.observability.event.interval.IntervalRecording;
import org.springframework.security.core.context.SecurityContext;

import static com.example.springsecurityinstrumentation.observability.event.SecurityContextInstantEvent.REPLACED;
import static com.example.springsecurityinstrumentation.observability.event.SecurityContextIntervalEvent.SECURITY_CONTEXT;

/**
 * Creates interval recordings for SecurityContext changed events.
 * In case of Sleuth, this will mean a new span for the security context.
 */
public class IntervalRecordingSecurityContextChangedListener extends AbstractSecurityContextChangedListener {
	private final static Logger LOGGER = LoggerFactory.getLogger(IntervalRecordingSecurityContextChangedListener.class);

	private final Recorder<?> recorder;
	private final Map<SecurityContext, IntervalRecording<?>> recordings;

	public IntervalRecordingSecurityContextChangedListener(Recorder<?> recorder) {
		this.recorder = recorder;
		// Since
		//     1. SecurityContext uses the wrapped Authentication object to calculate the hashcode and decide about equality
		//     2. SecurityContext is mutable
		// we can't use it as a key of a "normal" hashmap, we need to use IdentityHashMap
		// since the same object can have different hashcode/equality behavior in different points of time
		this.recordings = Collections.synchronizedMap(new IdentityHashMap<>());
	}

	@Override
	void handleCreateContextEvent(SecurityContext context) {
		IntervalRecording<?> recording = recorder.recordingFor(SECURITY_CONTEXT).start();
		recordings.put(context, recording);
	}

	@Override
	void handleClearContextEvent(SecurityContext context) {
		IntervalRecording<?> recording = recordings.remove(context);
		if (recording != null) {
			recording.stop();
		}
		else {
			LOGGER.warn("Recording not found for context: " + context);
		}
	}

	@Override
	void handleReplaceContextEvent(SecurityContext previousContext, SecurityContext currentContext) {
		IntervalRecording<?> recording = recordings.remove(previousContext);
		if (recording != null) {
			recordings.put(currentContext, recording);
			recorder.recordingFor(REPLACED)
					.detailedName(REPLACED.getName() + " " + getDetailedAuthEventName(previousContext.getAuthentication(), currentContext.getAuthentication()))
					.record();
		}
		else {
			LOGGER.warn("Recording not found for context: " + previousContext);
		}
	}

	@Override
	void handleNoContextEvent() {
		LOGGER.info("NO_AUTH");
	}
}
