package com.laboki.eclipse.plugin.smartsave.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class DisableSmartSaveHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		new Task() {

			@Override
			public void execute() {
				EditorContext.setCanSaveAutomatically(false);
			}
		}.start();
		return null;
	}
}
