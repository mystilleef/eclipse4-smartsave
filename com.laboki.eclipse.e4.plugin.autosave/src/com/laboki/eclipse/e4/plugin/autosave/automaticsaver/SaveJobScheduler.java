package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

final class SaveJobScheduler extends Job {

	private static final int TO_MILLISECONDS = 1000;
	private final SaveDecider decider = new SaveDecider();

	public SaveJobScheduler(final String name) {
		super(name);
		this.setPriority(Job.DECORATE);
	}

	void save() {
		this.decider.save();
	}

	void start() {
		this.schedule(ActivePart.getSaveIntervalInSeconds() * SaveJobScheduler.TO_MILLISECONDS);
	}

	void stop() {
		this.cancel();
	}

	@Override
	public boolean shouldSchedule() {
		return super.shouldSchedule() && this.checkJobPreconditions();
	}

	@Override
	public boolean shouldRun() {
		return super.shouldRun() && this.checkJobPreconditions();
	}

	private boolean checkJobPreconditions() {
		if (this.getState() == Job.WAITING) return false;
		if (this.getState() == Job.SLEEPING) return false;
		return true;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		Display.getDefault().asyncExec(this.new SaveJobRunnable());
		return Status.OK_STATUS;
	}

	private final class SaveJobRunnable implements Runnable {

		public SaveJobRunnable() {}

		@Override
		public void run() {
			SaveJobScheduler.this.save();
		}
	}
}
