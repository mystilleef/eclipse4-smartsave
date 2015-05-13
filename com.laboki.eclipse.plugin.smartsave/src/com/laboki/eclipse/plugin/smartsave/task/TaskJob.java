package com.laboki.eclipse.plugin.smartsave.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public abstract class TaskJob extends Job {

	private static final String NAME = "SmartSavePluginJob";
	private Object family;

	public TaskJob() {
		super(TaskJob.NAME);
	}

	@Override
	public boolean
	belongsTo(final Object family) {
		if (this.familyIsNull(family)) return false;
		return this.family.equals(family);
	}

	private boolean
	familyIsNull(final Object family) {
		return (this.family == null) || (family == null);
	}

	@Override
	protected IStatus
	run(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		this.runTask();
		return Status.OK_STATUS;
	}

	abstract void
	runTask();

	public void
	setFamily(final Object family) {
		this.family = family;
	}
}
