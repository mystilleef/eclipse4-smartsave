package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.PartChangedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class ListenerSwitch extends AbstractEventBusInstance {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	@Subscribe
	public void
	toggleListeners(final PartChangedEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				ListenerSwitch.this.toggleSaverListeners();
			}
		}.start();
	}

	@Subscribe
	public void
	toggleListeners(final AssistSessionEndedEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				ListenerSwitch.this.toggleSaverListeners();
			}
		}.start();
	}

	@Subscribe
	public static void
	disableListeners(final AssistSessionStartedEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				ListenerSwitch.postDisableListenersEvent();
			}
		}.start();
	}

	void
	toggleSaverListeners() {
		if (!this.editor.isPresent()) return;
		if (this.editor.get().isDirty()) ListenerSwitch.postEnableListenersEvent();
		else ListenerSwitch.postDisableListenersEvent();
	}

	static void
	postDisableListenersEvent() {
		EventBus.post(new DisableSaveListenersEvent());
	}

	private static void
	postEnableListenersEvent() {
		EventBus.post(new EnableSaveListenersEvent());
	}
}
