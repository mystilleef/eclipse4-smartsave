package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.Task;

final class FileSyncer extends AbstractEventBusInstance {

	private final IEditorPart editor = EditorContext.getEditor();
	private boolean completionAssistantIsActive;

	public FileSyncer(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void save(@SuppressWarnings("unused") final AssistSessionStartedEvent event) {
		this.completionAssistantIsActive = true;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void save(@SuppressWarnings("unused") final AssistSessionEndedEvent event) {
		this.completionAssistantIsActive = false;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void syncFiles(@SuppressWarnings("unused") final SyncFilesEvent event) {
		new Task(EditorContext.FILE_SYNCER_TASK, EditorContext.getSaveIntervalInMilliSeconds() - 1000) {

			@Override
			public boolean shouldSchedule() {
				if (FileSyncer.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK, EditorContext.SCHEDULED_SAVER_TASK);
			}

			@Override
			public boolean shouldRun() {
				if (FileSyncer.this.completionAssistantIsActive) return false;
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK, EditorContext.SCHEDULED_SAVER_TASK);
			}

			@Override
			public void execute() {
				EditorContext.syncFile(FileSyncer.this.editor);
				FileSyncer.this.eventBus.post(new StartSaveScheduleEvent());
			}
		}.begin();
	}
}
