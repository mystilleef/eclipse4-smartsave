package com.laboki.eclipse.plugin.smartsave.saver;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;
import com.laboki.eclipse.plugin.smartsave.AsyncTask;

public final class EventBus {

	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	private final AsyncEventBus bus = new AsyncEventBus(EventBus.EXECUTOR);

	public EventBus() {}

	public void register(final Object object) {
		this.bus.register(object);
	}

	public void unregister(final Object object) {
		this.bus.unregister(object);
	}

	public void post(final Object object) {
		EditorContext.asyncExec(new AsyncTask("") {

			@Override
			public void execute() {
				EventBus.this.bus.post(object);
			}
		});
	}
}
