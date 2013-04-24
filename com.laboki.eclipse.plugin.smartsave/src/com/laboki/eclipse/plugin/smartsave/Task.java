package com.laboki.eclipse.plugin.smartsave;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public abstract class Task extends Job implements Runnable {

	private final String name;

	public Task(final String name) {
		super(name);
		this.name = name;
		this.setPriority(Job.INTERACTIVE);
	}

	@Override
	public void run() {
		this.schedule();
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		Task.this.execute();
		return Status.OK_STATUS;
	}

	@Override
	public boolean belongsTo(final Object family) {
		return this.name.equals(family);
	}

	protected void execute() {}
}
