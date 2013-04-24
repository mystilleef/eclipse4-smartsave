package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.DelayedTask;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.Task;
import com.laboki.eclipse.plugin.smartsave.saver.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.FinishedSyncFilesEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.ScheduleSaveOnIdleEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.SyncFilesEvent;

public final class BusyDetector implements Instance, IJobChangeListener {

	private final EventBus eventBus;
	private boolean isBusy;

	public BusyDetector(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleSave(@SuppressWarnings("unused") final ScheduleSaveOnIdleEvent event) {
		EditorContext.asyncExec(new DelayedTask(EditorContext.SCHEDULED_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void execute() {
				if (this.isBusy()) EditorContext.scheduleSave(BusyDetector.this.eventBus, EditorContext.getSaveIntervalInSeconds());
				else BusyDetector.this.eventBus.post(new SyncFilesEvent());
			}

			private boolean isBusy() {
				return BusyDetector.this.isBusy || EditorContext.uiThreadIsBusy();
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

	@Subscribe
	@AllowConcurrentEvents
	public void startListening(@SuppressWarnings("unused") final EnableSaveListenersEvent event) {
		EditorContext.JOB_MANAGER.addJobChangeListener(this);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void stopListening(@SuppressWarnings("unused") final DisableSaveListenersEvent event) {
		EditorContext.JOB_MANAGER.removeJobChangeListener(this);
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		EditorContext.JOB_MANAGER.addJobChangeListener(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		EditorContext.JOB_MANAGER.removeJobChangeListener(this);
		return this;
	}

	@Override
	public void aboutToRun(final IJobChangeEvent arg0) {
		this.isBusy = true;
	}

	@Override
	public void awake(final IJobChangeEvent arg0) {}

	@Override
	public void done(final IJobChangeEvent arg0) {
		this.isBusy = false;
	}

	@Override
	public void running(final IJobChangeEvent arg0) {
		this.isBusy = true;
	}

	@Override
	public void scheduled(final IJobChangeEvent arg0) {}

	@Override
	public void sleeping(final IJobChangeEvent arg0) {}
}
