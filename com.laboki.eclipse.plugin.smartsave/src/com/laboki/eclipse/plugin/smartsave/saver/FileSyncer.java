package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.Task;
import com.laboki.eclipse.plugin.smartsave.saver.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.SyncFilesEvent;

final class FileSyncer implements Instance {

	private final EventBus eventBus;

	public FileSyncer(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void syncFiles(@SuppressWarnings("unused") final SyncFilesEvent event) {
		new Task(EditorContext.SCHEDULED_SAVER_TASK, EditorContext.getSaveIntervalInMilliSeconds()) {

			private final IEditorPart editor = EditorContext.getEditor();

			@Override
			public void execute() {
				EditorContext.syncFile(this.editor);
			}

			@Override
			public void postExecute() {
				FileSyncer.this.eventBus.post(new StartSaveScheduleEvent());
			}
		}.begin();
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
