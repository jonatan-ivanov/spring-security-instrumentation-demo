package com.example.springsecurityinstrumentation.observability;

import java.util.concurrent.TimeUnit;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.observability.event.instant.InstantRecording;
import org.springframework.observability.event.interval.IntervalRecording;
import org.springframework.observability.event.listener.RecordingListener;

public class SleuthRecordingListener implements RecordingListener<SleuthRecordingListener.TracingContext> {
	private final Tracer tracer;

	public SleuthRecordingListener(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	public void onStart(IntervalRecording<TracingContext> intervalRecording) {
		Span span = this.tracer.nextSpan()
				.name(intervalRecording.getDetailedName())
				.start(); // lie
//				.start(getStartTimeInMicros(intervalRecording));
		intervalRecording.getContext().setSpanAndScope(span, this.tracer.withSpan(span));
	}

	@Override
	public void onStop(IntervalRecording<TracingContext> intervalRecording) {
		SpanAndScope spanAndScope = intervalRecording.getContext().getSpanAndScope();
		Span span = spanAndScope.getSpan().name(intervalRecording.getDetailedName());
		intervalRecording.getTags().forEach(tag -> span.tag(tag.getKey(), tag.getValue()));
		spanAndScope.getScope().close();
		span.end(); // lie
//		span.end(getStopTimeInMicros(intervalRecording));
	}

	@Override
	public void onError(IntervalRecording<TracingContext> intervalRecording) {
		Span span = intervalRecording.getContext().getSpanAndScope().getSpan();
		span.error(intervalRecording.getError());
	}

	@Override
	public void record(InstantRecording instantRecording) {
		Span span = this.tracer.currentSpan();
		if (span != null) {
			instantRecording.getTags().forEach(tag -> span.tag(tag.getKey(), tag.getValue()));
			span.event(instantRecording.getDetailedName());
		}
	}

	@Override
	public TracingContext createContext() {
		return new TracingContext();
	}

	private long getStartTimeInMicros(IntervalRecording<TracingContext> recording) {
		return TimeUnit.NANOSECONDS.toMicros(recording.getStartWallTime());
	}

	private long getStopTimeInMicros(IntervalRecording<TracingContext> recording) {
		return TimeUnit.NANOSECONDS.toMicros(recording.getStartWallTime() + recording.getDuration().toNanos());
	}

	static class TracingContext {
		private SpanAndScope spanAndScope;

		SpanAndScope getSpanAndScope() {
			return this.spanAndScope;
		}

		void setSpanAndScope(Span span, Tracer.SpanInScope spanInScope) {
			this.spanAndScope = new SpanAndScope(span, spanInScope);
		}

	}

	static class SpanAndScope {
		private final Span span;
		private final Tracer.SpanInScope scope;

		SpanAndScope(Span span, Tracer.SpanInScope scope) {
			this.span = span;
			this.scope = scope;
		}

		Span getSpan() {
			return this.span;
		}

		Tracer.SpanInScope getScope() {
			return this.scope;
		}

		@Override
		public String toString() {
			return "SpanAndScope{" + "span=" + this.span + '}';
		}
	}
}
