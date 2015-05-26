package com.laboki.eclipse.plugin.smartsave.checkers;

import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.AnnotationContext;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.CheckWarningEvent;
import com.laboki.eclipse.plugin.smartsave.events.SaveEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.main.Scheduler;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class WarningChecker extends EventBusInstance {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final CheckWarningEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				if (this.editorHasNoWarnings()) this.broadcastEvent();
			}

			private boolean
			editorHasNoWarnings() {
				return !this.editorHasWarnings();
			}

			private boolean
			editorHasWarnings() {
				return this.canCheckWarning() && this.hasWarnings();
			}

			private boolean
			canCheckWarning() {
				return !Store.getCanSaveIfWarnings();
			}

			private boolean
			hasWarnings() {
				return AnnotationContext.hasWarnings(WarningChecker.this.editor);
			}

			private void
			broadcastEvent() {
				EventBus.post(new SaveEvent());
			}
		}.setDelay(Scheduler.DELAY)
			.setFamily(Scheduler.FAMILY)
			.setRule(Scheduler.RULE)
			.start();
	}
}
