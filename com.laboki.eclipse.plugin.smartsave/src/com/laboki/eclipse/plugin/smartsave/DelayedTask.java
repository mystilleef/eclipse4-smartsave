package com.laboki.eclipse.plugin.smartsave;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

public abstract class DelayedTask extends Job implements Runnable {

	private final int timeInMilliSeconds;
	private final String name;

	public DelayedTask(final String name, final int timeInMilliSeconds) {
		super(name);
		this.name = name;
		this.timeInMilliSeconds = timeInMilliSeconds;
	}

	@Override
	public void run() {
		this.schedule(this.timeInMilliSeconds);
	}

	@Override
	protected IStatus run(final IProgressMonitor arg0) {
		EditorContext.asyncExec(new Runnable() {

			@Override
			public void run() {
				DelayedTask.this.execute();
			}
		});
		return Status.OK_STATUS;
	}

	@Override
	public boolean belongsTo(final Object family) {
		return this.name.equals(family);
	}

	protected void execute() {}
}
