package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;

final class SaveCountDownTimer implements Runnable {

	private ScheduledFuture<?> schedulerHandle;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final SaveDecider saveDecider = new SaveDecider();
	private final int saveIntervalInSeconds = 5;

	@Override
	public void run() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				SaveCountDownTimer.this.getSaveDecider().save();
			}
		});
	}

	void save() {
		this.run();
	}

	private void start() {
		this.schedulerHandle = this.scheduler.schedule(this, this.saveIntervalInSeconds, TimeUnit.SECONDS);
	}

	void stop() {
		if (this.noRunningTasks()) return;
		this.schedulerHandle.cancel(true);
	}

	private boolean noRunningTasks() {
		return (this.schedulerHandle == null) || this.schedulerHandle.isCancelled() || this.schedulerHandle.isDone();
	}

	void restart() {
		this.stop();
		this.start();
	}

	public SaveDecider getSaveDecider() {
		return this.saveDecider;
	}
}
