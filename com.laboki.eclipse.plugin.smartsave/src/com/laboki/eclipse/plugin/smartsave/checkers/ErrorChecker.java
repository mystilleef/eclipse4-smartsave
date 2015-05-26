package com.laboki.eclipse.plugin.smartsave.checkers;

import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.AnnotationContext;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.CheckErrorEvent;
import com.laboki.eclipse.plugin.smartsave.events.CheckWarningEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.main.Scheduler;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class ErrorChecker extends EventBusInstance {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final CheckErrorEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				if (this.editorHasNoErrors()) this.broadcastEvent();
			}

			private boolean
			editorHasNoErrors() {
				return !this.editorHasErrors();
			}

			private boolean
			editorHasErrors() {
				if (this.canCheckError()) return this.hasErrors();
				return false;
			}

			private boolean
			canCheckError() {
				return Store.getCanSaveIfErrors();
			}

			private boolean
			hasErrors() {
				return AnnotationContext.hasErrors(ErrorChecker.this.editor);
			}

			private void
			broadcastEvent() {
				EventBus.post(new CheckWarningEvent());
			}
		}.setDelay(Scheduler.DELAY)
			.setFamily(Scheduler.FAMILY)
			.setRule(Scheduler.RULE)
			.start();
	}
}
