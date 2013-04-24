package com.laboki.eclipse.plugin.smartsave.saver;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.AsyncDelayedTask;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.saver.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.ScheduleSaveOnIdleEvent;

public final class Scheduler implements Instance {

	private final EventBus eventBus;

	public Scheduler(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleSave(@SuppressWarnings("unused") final ScheduleSaveEvent event) {
		EditorContext.asyncExec(new AsyncDelayedTask(EditorContext.SCHEDULED_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void execute() {
				Scheduler.cancelAllJobs();
				Scheduler.this.eventBus.post(new ScheduleSaveOnIdleEvent());
			}
		});
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleSave(@SuppressWarnings("unused") final EnableSaveListenersEvent event) {
		EditorContext.asyncExec(new AsyncTask(EditorContext.SCHEDULED_SAVER_TASK) {

			@Override
			public void execute() {
				Scheduler.cancelAllJobs();
				EditorContext.scheduleSave(Scheduler.this.eventBus, EditorContext.getSaveIntervalInSeconds());
			}
		});
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void cancelSaveJobs(@SuppressWarnings("unused") final DisableSaveListenersEvent event) {
		Scheduler.cancelAllJobs();
	}

	private static void cancelAllJobs() {
		EditorContext.cancelAllJobs();
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		return this;
	}
}
