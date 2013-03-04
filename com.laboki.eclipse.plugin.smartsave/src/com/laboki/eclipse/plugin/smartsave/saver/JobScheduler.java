package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

final class JobScheduler extends Job {

	private static final EditorContext EDITOR = EditorContext.instance();
	private static final int TO_MILLISECONDS = 1000;
	private final Decider decider = new Decider();
	private final SaveJobRunnable saveJobRunnable = this.new SaveJobRunnable();

	public JobScheduler(final String name) {
		super(name);
		this.setPriority(Job.DECORATE);
	}

	public void save() {
		this.decider.save();
	}

	public void start() {
		this.schedule(JobScheduler.EDITOR.getSaveIntervalInSeconds() * JobScheduler.TO_MILLISECONDS);
	}

	public void stop() {
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
		EditorContext.asyncExec(this.saveJobRunnable);
		return Status.OK_STATUS;
	}

	private final class SaveJobRunnable implements Runnable {

		public SaveJobRunnable() {}

		@Override
		public void run() {
			JobScheduler.this.save();
		}
	}
}
