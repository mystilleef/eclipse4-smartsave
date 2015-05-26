package com.laboki.eclipse.plugin.smartsave.checkers;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.AnnotationContext;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.CheckBusyEvent;
import com.laboki.eclipse.plugin.smartsave.events.CheckErrorEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.main.Scheduler;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class BusyChecker extends EventBusInstance {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final CheckBusyEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				if (this.editorIsNotBusy()) this.broadcastEvent();
			}

			private boolean
			editorIsNotBusy() {
				return !this.editorIsBusy();
			}

			private boolean
			editorIsBusy() {
				if (this.editorHasSelections()) return true;
				return AnnotationContext.isInLinkMode(BusyChecker.this.editor);
			}

			private boolean
			editorHasSelections() {
				final Optional<StyledText> buffer =
					EditorContext.getBuffer(BusyChecker.this.editor);
				if (!buffer.isPresent()) return false;
				return (buffer.get().getSelectionCount() > 0)
					|| buffer.get().getBlockSelection();
			}

			private void
			broadcastEvent() {
				EventBus.post(new CheckErrorEvent());
			}
		}.setDelay(Scheduler.DELAY)
			.setFamily(Scheduler.FAMILY)
			.setRule(Scheduler.RULE)
			.start();
	}
}
