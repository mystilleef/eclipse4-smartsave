package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.Task;

final class FileSyncer extends AbstractEventBusInstance {

	private final IEditorPart editor = EditorContext.getEditor();

	public FileSyncer(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void syncFiles(@SuppressWarnings("unused") final SyncFilesEvent event) {
		new Task(EditorContext.SCHEDULED_SAVER_TASK, EditorContext.getSaveIntervalInMilliSeconds()) {

			@Override
			public boolean shouldSchedule() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public boolean shouldRun() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void execute() {
				EditorContext.syncFile(FileSyncer.this.editor);
				FileSyncer.this.eventBus.post(new StartSaveScheduleEvent());
			}
		}.begin();
	}
}
