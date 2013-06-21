package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.PartChangedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;

public final class ListenerToggler extends AbstractEventBusInstance {

	private final IEditorPart editor = EditorContext.getEditor();

	public ListenerToggler(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	public void toggleListeners(@SuppressWarnings("unused") final PartChangedEvent event) {
		this.toggleSaverListeners();
	}

	@Subscribe
	public void toggleListeners(@SuppressWarnings("unused") final AssistSessionEndedEvent event) {
		this.toggleSaverListeners();
	}

	@Subscribe
	public void disableListeners(@SuppressWarnings("unused") final AssistSessionStartedEvent event) {
		ListenerToggler.this.postDisableListenersEvent();
	}

	private void toggleSaverListeners() {
		if (this.editor.isDirty()) this.postEnableListenersEvent();
		else this.postDisableListenersEvent();
	}

	private void postDisableListenersEvent() {
		EditorContext.cancelAllJobs();
		this.eventBus.post(new DisableSaveListenersEvent());
	}

	private void postEnableListenersEvent() {
		EditorContext.cancelAllJobs();
		this.eventBus.post(new EnableSaveListenersEvent());
	}
}
