package com.laboki.eclipse.plugin.smartsave;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

public abstract class Task extends Job implements Runnable {

	public static final int TASK_INTERACTIVE = Job.INTERACTIVE;
	public static final int TASK_SHORT = Job.SHORT;
	public static final int TASK_LONG = Job.LONG;
	public static final int TASK_BUILD = Job.BUILD;
	public static final int TASK_DECORATE = Job.DECORATE;
	private final int delayTime;
	private final String name;

	protected Task() {
		super("");
		this.name = "";
		this.delayTime = 0;
		this.setPriority(Task.TASK_INTERACTIVE);
	}

	protected Task(final String name) {
		super(name);
		this.name = name;
		this.delayTime = 0;
		this.setPriority(Task.TASK_INTERACTIVE);
	}

	protected Task(final String name, final int delayTime) {
		super(name);
		this.name = name;
		this.delayTime = delayTime;
		this.setPriority(Task.TASK_DECORATE);
	}

	protected Task(final String name, final int delayTime, final int priority) {
		super(name);
		this.name = name;
		this.delayTime = delayTime;
		this.setPriority(priority);
	}

	@Override
	public boolean belongsTo(final Object family) {
		return this.name.equals(family);
	}

	@Override
	public void run() {
		this.setUser(false);
		this.setSystem(true);
		this.schedule(this.delayTime);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		this.runTask();
		return Status.OK_STATUS;
	}

	private void runTask() {
		this.execute();
		this.runExec();
		this.postExecute();
	}

	private void runExec() {
		this.runAsyncExec();
	}

	protected void execute() {}

	private void runAsyncExec() {
		EditorContext.asyncExec(new Runnable() {

			@Override
			public void run() {
				EditorContext.flushEvents();
				Task.this.asyncExec();
			}
		});
	}

	protected void asyncExec() {}

	protected void syncExec() {}

	protected void postExecute() {}
}
