package com.laboki.eclipse.plugin.smartsave.checkers;

import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.CheckBusyEvent;
import com.laboki.eclipse.plugin.smartsave.events.CheckDirtyEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.main.Scheduler;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class DirtyChecker extends EventBusInstance {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final CheckDirtyEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				if (this.editorPartIsDirty()) this.broadcastEvent();
			}

			private boolean
			editorPartIsDirty() {
				if (!DirtyChecker.this.editor.isPresent()) return false;
				return DirtyChecker.this.editor.get().isDirty();
			}

			private void
			broadcastEvent() {
				EventBus.post(new CheckBusyEvent());
			}
		}.setDelay(Scheduler.DELAY)
			.setFamily(Scheduler.FAMILY)
			.setRule(Scheduler.RULE)
			.start();
	}
}
