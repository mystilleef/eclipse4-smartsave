package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.Task;
import com.laboki.eclipse.plugin.smartsave.saver.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.PartChangedEvent;

public final class ListenerToggler implements Instance {

	private final EventBus eventBus;
	private final IEditorPart editor = EditorContext.getEditor();

	public ListenerToggler(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void toggleListeners(@SuppressWarnings("unused") final PartChangedEvent event) {
		this.asyncToggleSaverListeners();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void toggleListeners(@SuppressWarnings("unused") final AssistSessionEndedEvent event) {
		this.asyncToggleSaverListeners();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void disableListeners(@SuppressWarnings("unused") final AssistSessionStartedEvent event) {
		EditorContext.asyncExec(new Task(EditorContext.SCHEDULED_SAVER_TASK) {

			@Override
			public void execute() {
				ListenerToggler.this.postDisableListenersEvent();
			}
		});
	}

	private void asyncToggleSaverListeners() {
		EditorContext.asyncExec(new Task(EditorContext.SCHEDULED_SAVER_TASK) {

			@Override
			public void asyncExec() {
				ListenerToggler.this.toggleSaverListeners();
			}
		});
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

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		return this;
	}
}
