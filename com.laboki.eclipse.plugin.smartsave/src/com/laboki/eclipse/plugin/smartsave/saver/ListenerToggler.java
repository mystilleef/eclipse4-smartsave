package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.PartChangedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class ListenerToggler extends AbstractEventBusInstance {

	private final IEditorPart editor = EditorContext.getEditor();

	public ListenerToggler(final EventBus eventBus) {
		super(eventBus);
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
		new Task(EditorContext.SCHEDULED_SAVER_TASK) {

			@Override
			public void execute() {
				ListenerToggler.this.postDisableListenersEvent();
			}
		}.begin();
	}

	private void asyncToggleSaverListeners() {
		new AsyncTask(EditorContext.SCHEDULED_SAVER_TASK) {

			@Override
			public void asyncExecute() {
				ListenerToggler.this.toggleSaverListeners();
			}
		}.begin();
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
