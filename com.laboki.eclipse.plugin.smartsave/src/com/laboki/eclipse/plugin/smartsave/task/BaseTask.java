package com.laboki.eclipse.plugin.smartsave.task;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.smartsave.instance.Instance;

public abstract class BaseTask implements Runnable, Instance, ExecuteTask {

	public static final String FAMILY = "SmartSavePluginTaskFamily";
	private final TaskJob job;
	private long delay = 0;

	public BaseTask() {
		this.job = this.newTaskJob();
		this.setDefaultProperties();
	}

	protected abstract TaskJob
	newTaskJob();

	private void
	setDefaultProperties() {
		this.job.setUser(false);
		this.job.setSystem(true);
		this.job.setFamily(BaseTask.FAMILY);
	}

	@Override
	public void
	run() {
		this.start();
	}

	@Override
	public Instance
	start() {
		if (!this.shouldSchedule()) return this;
		this.job.schedule(this.delay);
		return this;
	}

	@Override
	public Instance
	stop() {
		this.job.cancel();
		return this;
	}

	public BaseTask
	setDelay(final long delay) {
		this.delay = delay;
		return this;
	}

	public BaseTask
	setFamily(final Object family) {
		this.job.setFamily(family);
		return this;
	}

	public BaseTask
	setName(final String name) {
		this.job.setName(name);
		return this;
	}

	public BaseTask
	setPriority(final int priority) {
		this.job.setPriority(priority);
		return this;
	}

	public BaseTask
	setUser(final boolean isUserTask) {
		this.job.setUser(isUserTask);
		return this;
	}

	public BaseTask
	setSystem(final boolean isSystemTask) {
		this.job.setSystem(isSystemTask);
		return this;
	}

	public BaseTask
	setRule(final ISchedulingRule rule) {
		this.job.setRule(rule);
		return this;
	}

	protected void
	reschedule(final long delay) {
		this.job.schedule(delay);
		return;
	}

	protected boolean
	belongsTo(final Object family) {
		return this.job.belongsTo(family);
	}

	protected boolean
	shouldSchedule() {
		return this.job.shouldSchedule();
	}

	protected static boolean
	taskFamilyExists(final Object family) {
		return Job.getJobManager().find(family).length > 0;
	}

	protected static boolean
	noTaskFamilyExists(final Object family) {
		return !BaseTask.taskFamilyExists(family);
	}
}
