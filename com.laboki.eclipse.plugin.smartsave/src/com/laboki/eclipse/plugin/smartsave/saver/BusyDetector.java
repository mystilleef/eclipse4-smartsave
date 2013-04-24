package com.laboki.eclipse.plugin.smartsave.saver;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.DelayedTask;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.Task;
import com.laboki.eclipse.plugin.smartsave.saver.events.FinishedSyncFilesEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.ScheduleSaveOnIdleEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.SyncFilesEvent;

public final class BusyDetector implements Instance {

	private final EventBus eventBus;

	public BusyDetector(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleSave(@SuppressWarnings("unused") final ScheduleSaveOnIdleEvent event) {
		EditorContext.asyncExec(new DelayedTask(EditorContext.SCHEDULED_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void execute() {
				if (EditorContext.isBusy()) EditorContext.scheduleSave(BusyDetector.this.eventBus);
				else BusyDetector.this.eventBus.post(new SyncFilesEvent());
			}
		});
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleSave(@SuppressWarnings("unused") final FinishedSyncFilesEvent event) {
		EditorContext.asyncExec(new Task(EditorContext.SCHEDULED_SAVER_TASK) {

			@Override
			public void execute() {
				BusyDetector.this.eventBus.post(new StartSaveScheduleEvent());
			}
		});
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
