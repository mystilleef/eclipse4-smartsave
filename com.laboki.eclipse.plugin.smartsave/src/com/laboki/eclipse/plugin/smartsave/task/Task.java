package com.laboki.eclipse.plugin.smartsave.task;

public abstract class Task extends BaseTask implements ExecuteTask {

	@Override
	protected TaskJob
	newTaskJob() {
		return new TaskJob() {

			@Override
			protected void
			runTask() {
				Task.this.execute();
			}
		};
	}

	@Override
	public abstract void
	execute();
}
