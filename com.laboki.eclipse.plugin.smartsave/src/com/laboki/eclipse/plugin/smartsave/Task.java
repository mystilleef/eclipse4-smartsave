package com.laboki.eclipse.plugin.smartsave;

public class Task extends AbstractTask implements Instance {

	public Task() {
		super("", 0, AbstractTask.TASK_INTERACTIVE);
	}

	public Task(final String name) {
		super(name, 0, AbstractTask.TASK_INTERACTIVE);
	}

	public Task(final int delayTime) {
		super("", delayTime, AbstractTask.TASK_DECORATE);
	}

	public Task(final String name, final int delayTime) {
		super(name, delayTime, AbstractTask.TASK_DECORATE);
	}

	public Task(final String name, final int delayTime, final int priority) {
		super(name, delayTime, priority);
	}

	@Override
	public Instance begin() {
		this.run();
		return this;
	}

	@Override
	public Instance end() {
		this.cancel();
		return this;
	}
}
