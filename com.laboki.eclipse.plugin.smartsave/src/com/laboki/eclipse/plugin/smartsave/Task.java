package com.laboki.eclipse.plugin.smartsave;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

public abstract class Task extends Job implements Runnable {

	private final String name;

	public Task(final String name) {
		super(name);
		this.name = name;
		this.setPriority(Job.INTERACTIVE);
	}

	@Override
	public void run() {
		this.setUser(false);
		this.setSystem(true);
		this.schedule();
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
				Task.this.asyncExec();
			}
		});
	}

	private void runSyncExec() {
		EditorContext.syncExec(new Runnable() {

			@Override
			public void run() {
				Task.this.syncExec();
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
