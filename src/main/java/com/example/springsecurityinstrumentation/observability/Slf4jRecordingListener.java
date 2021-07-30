package com.example.springsecurityinstrumentation.observability;

import org.slf4j.Logger;

import org.springframework.observability.event.instant.InstantRecording;
import org.springframework.observability.event.interval.IntervalRecording;
import org.springframework.observability.event.listener.RecordingListener;

public class Slf4jRecordingListener implements RecordingListener<Void> {
	private final Logger logger;

	public Slf4jRecordingListener(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void onStart(IntervalRecording<Void> intervalRecording) {
		logger.info("Started: {}", intervalRecording);
	}

	@Override
	public void onStop(IntervalRecording<Void> intervalRecording) {
		logger.info("Stopped: {}", intervalRecording);
	}

	@Override
	public void onError(IntervalRecording<Void> intervalRecording) {
		logger.error("Oops, an error occurred!", intervalRecording.getError());
	}

	@Override
	public void record(InstantRecording instantRecording) {
		logger.info("Event: {}", instantRecording);
	}

	@Override
	public Void createContext() {
		return null;
	}
}
