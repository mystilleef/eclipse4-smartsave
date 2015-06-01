package com.laboki.eclipse.plugin.smartsave.main;

import java.util.concurrent.Executors;

import org.eclipse.core.runtime.jobs.Job;

import com.google.common.eventbus.AsyncEventBus;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public enum EventBus {
	INSTANCE;

	public static final String FAMILY = "+SmartSaveEventBusTaskFamily+";
	protected static final AsyncEventBus BUS =
		new AsyncEventBus(Executors.newCachedThreadPool());

	public static void
	register(final Object object) {
		EventBus.BUS.register(object);
	}

	public static void
	unregister(final Object object) {
		EventBus.BUS.unregister(object);
	}

	public static void
	post(final Object object) {
		new Task() {

			@Override
			public void
			execute() {
				EventBus.BUS.post(object);
			}
		}.setFamily(EventBus.FAMILY).setPriority(Job.INTERACTIVE).start();
	}
}
