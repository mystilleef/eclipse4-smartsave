package com.laboki.eclipse.plugin.smartsave.task;

import org.eclipse.core.runtime.jobs.Job;

public class SyncTask extends AbstractTask {

	public SyncTask() {
		super("", 0, Job.INTERACTIVE);
	}

	public SyncTask(final String name) {
		super(name, 0, Job.INTERACTIVE);
	}

	public SyncTask(final int delayTime) {
		super("", delayTime, Job.DECORATE);
	}

	public SyncTask(final String name, final int delayTime) {
		super(name, delayTime, Job.DECORATE);
	}

	public SyncTask(final String name, final int delayTime, final int priority) {
		super(name, delayTime, priority);
	}

	@Override
	protected void runTask() {
		this.runSyncExecute();
	}
}
