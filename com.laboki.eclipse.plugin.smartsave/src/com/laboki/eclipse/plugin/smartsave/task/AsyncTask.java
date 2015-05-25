package com.laboki.eclipse.plugin.smartsave.task;

import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;

public abstract class AsyncTask extends Task {

	@Override
	protected TaskJob
	newTaskJob() {
		return new TaskJob() {

			@Override
			protected void
			runTask() {
				EditorContext.asyncExec(() -> AsyncTask.this.execute());
			}
		};
	}
}
