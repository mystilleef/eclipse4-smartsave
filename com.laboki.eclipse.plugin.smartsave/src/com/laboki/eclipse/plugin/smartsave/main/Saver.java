package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class Saver extends EventBusInstance {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final StartSaveScheduleEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				if (!Saver.this.editor.isPresent()) return;
				EditorContext.save(Saver.this.editor);
			}
		}.setFamily(Scheduler.FAMILY)
			.setDelay(Scheduler.DELAY)
			.setRule(Scheduler.RULE)
			.start();
	}
}
