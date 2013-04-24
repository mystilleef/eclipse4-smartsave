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
		this.setPriority(Job.DECORATE);
	}

	@Override
	public void run() {
		this.setUser(false);
		this.setSystem(true);
		this.schedule(this.timeInMilliSeconds);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		this.execute();
		this.runAsyncExec();
		this.runSyncExec();
		this.postExecute();
		return Status.OK_STATUS;
	}

	private void runAsyncExec() {
		EditorContext.asyncExec(new Runnable() {

			@Override
			public void run() {
				DelayedTask.this.asyncExec();
			}
		});
	}

	private void runSyncExec() {
		EditorContext.syncExec(new Runnable() {

			@Override
			public void run() {
				DelayedTask.this.syncExec();
			}
		});
	}

	@Override
	public boolean belongsTo(final Object family) {
		return this.name.equals(family);
	}

	protected void execute() {}

	protected void asyncExec() {}

	protected void syncExec() {}

	protected void postExecute() {}
}
