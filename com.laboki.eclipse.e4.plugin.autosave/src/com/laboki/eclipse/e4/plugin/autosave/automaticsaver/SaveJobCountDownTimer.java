package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

final class SaveJobCountDownTimer extends Job {

	private static final int MILLI_SECOND_UNITS = 1000;

	public SaveJobCountDownTimer(final String saveJob) {
		super(saveJob);
		this.setPriority(Job.DECORATE);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				SaveJobCountDownTimer.save();
			}
		});
		return Status.OK_STATUS;
	}

	static void save() {
		SaveDecider.save();
	}

	private void start() {
		this.schedule(ActivePart.getSaveIntervalInSeconds() * SaveJobCountDownTimer.MILLI_SECOND_UNITS);
	}

	void stop() {
		this.cancel();
		// if (!this.cancel()) try {
		// this.join();
		// } catch (final InterruptedException e) {
		// e.printStackTrace();
		// }
	}

	void restart() {
		this.stop();
		this.start();
	}
}
