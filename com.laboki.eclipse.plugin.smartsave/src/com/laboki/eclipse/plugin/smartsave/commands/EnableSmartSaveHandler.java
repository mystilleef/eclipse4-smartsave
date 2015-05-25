package com.laboki.eclipse.plugin.smartsave.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class EnableSmartSaveHandler extends AbstractHandler {

	private static final TaskMutexRule RULE = new TaskMutexRule();

	@Override
	public Object
	execute(final ExecutionEvent event) throws ExecutionException {
		new Task() {

			@Override
			public void
			execute() {
				EditorContext.setCanSaveAutomatically(true);
			}
		}.setRule(EnableSmartSaveHandler.RULE)
			.setPriority(Job.INTERACTIVE)
			.start();
		return null;
	}
}
