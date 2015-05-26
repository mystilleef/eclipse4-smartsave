package com.laboki.eclipse.plugin.smartsave.checkers;

import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.CheckBlacklistEvent;
import com.laboki.eclipse.plugin.smartsave.events.CheckDirtyEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.main.Scheduler;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class BlacklistCheckers extends EventBusInstance {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final CheckBlacklistEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				if (this.fileIsNotBlacklisted()) this.broadcastEvent();
			}

			private boolean
			fileIsNotBlacklisted() {
				return !this.fileIsBlacklisted();
			}

			private boolean
			fileIsBlacklisted() {
				return EditorContext.isBlacklisted(BlacklistCheckers.this.editor);
			}

			private void
			broadcastEvent() {
				EventBus.post(new CheckDirtyEvent());
			}
		}.setDelay(Scheduler.DELAY)
			.setFamily(Scheduler.FAMILY)
			.setRule(Scheduler.RULE)
			.start();
	}
}
