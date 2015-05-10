package com.laboki.eclipse.plugin.smartsave.task;

import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public abstract class SyncTask extends BaseTask implements ExecuteTask {

	@Override
	protected TaskJob
	newTaskJob() {
		return new TaskJob() {

			@Override
			protected void
			runTask() {
				EditorContext.syncExec(() -> SyncTask.this.execute());
			}
		};
	}

	@Override
	public abstract void
	execute();
}
