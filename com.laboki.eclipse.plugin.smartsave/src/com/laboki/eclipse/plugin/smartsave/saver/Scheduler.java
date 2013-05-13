package com.laboki.eclipse.plugin.smartsave.saver;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class Scheduler implements Instance {

	private final EventBus eventBus;

	public Scheduler(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleSave(@SuppressWarnings("unused") final ScheduleSaveEvent event) {
		new Task(EditorContext.SCHEDULED_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void execute() {
				Scheduler.cancelAllJobs();
			}

			@Override
			public void postExecute() {
				Scheduler.this.eventBus.post(new SyncFilesEvent());
			}
		}.begin();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleSave(@SuppressWarnings("unused") final EnableSaveListenersEvent event) {
		new Task(EditorContext.SCHEDULED_SAVER_TASK) {

			@Override
			public void execute() {
				EditorContext.scheduleSave(Scheduler.this.eventBus, EditorContext.SHORT_DELAY_TIME);
			}
		}.begin();
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void cancelSaveJobs(@SuppressWarnings("unused") final DisableSaveListenersEvent event) {
		Scheduler.asyncCancelAllJobs();
	}

	private static void asyncCancelAllJobs() {
		new Task() {

			@Override
			public void execute() {
				Scheduler.cancelAllJobs();
			}
		}.begin();
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
