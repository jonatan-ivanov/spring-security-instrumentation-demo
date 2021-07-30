package com.example.springsecurityinstrumentation;

import com.example.springsecurityinstrumentation.observability.SleuthRecordingListener;
import com.example.springsecurityinstrumentation.observability.Slf4jRecordingListener;
import com.example.springsecurityinstrumentation.security.listener.InstantRecordingSecurityContextChangedListener;
import com.example.springsecurityinstrumentation.security.listener.SoutingSecurityContextChangedListener;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.observability.event.Recorder;
import org.springframework.observability.event.SimpleRecorder;
import org.springframework.observability.event.listener.composite.CompositeContext;
import org.springframework.observability.event.listener.composite.CompositeRecordingListener;
import org.springframework.observability.micrometer.listener.MicrometerRecordingListener;
import org.springframework.observability.time.Clock;
import org.springframework.security.core.context.SecurityContextChangedListener;

@Configuration
public class ObservabilityConfiguration {

	@Bean
	public SecurityContextChangedListener soutingSecurityContextChangedListener() {
		return new SoutingSecurityContextChangedListener();
	}

	@Bean
	public SecurityContextChangedListener instantRecordingSecurityContextChangedListener(Recorder<?> recorder) {
		return new InstantRecordingSecurityContextChangedListener(recorder);
	}

//	@Bean
//	public SecurityContextChangedListener intervalRecordingSecurityContextChangedListener(Recorder<?> recorder) {
//		return new IntervalRecordingSecurityContextChangedListener(recorder);
//	}

	@Bean
	public Recorder<CompositeContext> recorder(CompositeRecordingListener compositeRecordingListener) {
		return new SimpleRecorder<>(compositeRecordingListener, Clock.SYSTEM);
	}

	@Bean
	public CompositeRecordingListener compositeRecordingListener(Slf4jRecordingListener slf4jRecordingListener, SleuthRecordingListener sleuthRecordingListener, MicrometerRecordingListener micrometerRecordingListener) {
		return new CompositeRecordingListener(slf4jRecordingListener, sleuthRecordingListener, micrometerRecordingListener);
	}

	@Bean
	public Slf4jRecordingListener slf4jRecordingListener() {
		return new Slf4jRecordingListener(LoggerFactory.getLogger("recorder"));
	}

	@Bean
	public SleuthRecordingListener sleuthRecordingListener(Tracer tracer) {
		return new SleuthRecordingListener(tracer);
	}

	@Bean
	public MicrometerRecordingListener micrometerRecordingListener(MeterRegistry meterRegistry) {
		return new MicrometerRecordingListener(meterRegistry);
	}
}
