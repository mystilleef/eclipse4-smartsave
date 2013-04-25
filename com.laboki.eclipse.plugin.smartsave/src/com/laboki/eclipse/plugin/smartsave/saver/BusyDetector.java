package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
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
	@SuppressWarnings("unused") private boolean isBusy;

	public BusyDetector(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void scheduleSave(@SuppressWarnings("unused") final ScheduleSaveOnIdleEvent event) {
		EditorContext.asyncExec(new Task(EditorContext.SCHEDULED_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			protected void execute() {
				this.postEvent();
			}

			@Override
			public void asyncExec() {
				// if (this.isBusy())
				// EditorContext.scheduleSave(BusyDetector.this.eventBus,
				// EditorContext.getSaveIntervalInSeconds()
				// * 1000);
				// else postEvent();
			}

			private void postEvent() {
				BusyDetector.this.eventBus.post(new SyncFilesEvent());
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

	@SuppressWarnings("static-method")
	@Subscribe
	@AllowConcurrentEvents
	public void startListening(@SuppressWarnings("unused") final EnableSaveListenersEvent event) {
		EditorContext.asyncExec(new Task() {

			@Override
			public void asyncExec() {
				// EditorContext.JOB_MANAGER.addJobChangeListener(BusyDetector.this);
			}
		});
	}

	@Subscribe
	@AllowConcurrentEvents
	public void stopListening(@SuppressWarnings("unused") final DisableSaveListenersEvent event) {
		EditorContext.asyncExec(new Task() {

			@Override
			public void asyncExec() {
				// EditorContext.JOB_MANAGER.removeJobChangeListener(BusyDetector.this);
				BusyDetector.this.isBusy = false;
			}
		});
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
		// this.isBusy = true;
	}

	@Override
	public void awake(final IJobChangeEvent arg0) {}

	@Override
	public void done(final IJobChangeEvent arg0) {
		// this.isBusy = false;
	}

	@Override
	public void running(final IJobChangeEvent arg0) {
		// this.isBusy = true;
	}

	@Override
	public void scheduled(final IJobChangeEvent arg0) {}

	@Override
	public void sleeping(final IJobChangeEvent arg0) {}
}
