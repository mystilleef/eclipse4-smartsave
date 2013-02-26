package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

final class SaveJobCountDownTimer extends Job {

	private static final int TO_MILLISECONDS = 1000;
	private final SaveDecider decider = new SaveDecider();

	public SaveJobCountDownTimer(final String name) {
		super(name);
		this.setPriority(Job.DECORATE);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		Display.getDefault().asyncExec(this.new SaveJobRunnable());
		return Status.OK_STATUS;
	}

	void save() {
		this.decider.save();
	}

	private void start() {
		this.schedule(ActivePart.getSaveIntervalInSeconds() * SaveJobCountDownTimer.TO_MILLISECONDS);
	}

	void stop() {
		if (!this.cancel()) try {
			this.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	void restart() {
		this.stop();
		this.start();
	}

	private final class SaveJobRunnable implements Runnable {

		public SaveJobRunnable() {}

		@Override
		public void run() {
			SaveJobCountDownTimer.this.save();
		}
	}
}
