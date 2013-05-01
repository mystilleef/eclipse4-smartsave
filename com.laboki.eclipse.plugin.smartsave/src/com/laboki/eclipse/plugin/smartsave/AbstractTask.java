package com.laboki.eclipse.plugin.smartsave;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

abstract class AbstractTask extends Job implements Runnable, ITask {

	public static final int TASK_INTERACTIVE = Job.INTERACTIVE;
	public static final int TASK_SHORT = Job.SHORT;
	public static final int TASK_LONG = Job.LONG;
	public static final int TASK_BUILD = Job.BUILD;
	public static final int TASK_DECORATE = Job.DECORATE;
	private final int delayTime;
	private final String name;

	protected AbstractTask(final String name, final int delayTime, final int priority) {
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
		this.runAsyncExec();
		this.postExecute();
	}

	@Override
	public void execute() {}

	private void runAsyncExec() {
		EditorContext.asyncExec(new Runnable() {

			@Override
			public void run() {
				EditorContext.flushEvents();
				AbstractTask.this.asyncExec();
			}
		});
	}

	@Override
	public void asyncExec() {}

	@Override
	public void postExecute() {}
}
