package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class Saver extends AbstractEventBusInstance {

	private final IEditorPart editor = EditorContext.getEditor();

	public Saver(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void save(@SuppressWarnings("unused") final StartSaveScheduleEvent event) {
		new AsyncTask(EditorContext.AUTOMATIC_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public boolean shouldSchedule() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK, EditorContext.SCHEDULED_SAVER_TASK);
			}

			@Override
			public boolean shouldRun() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK, EditorContext.SCHEDULED_SAVER_TASK);
			}

			@Override
			public void asyncExecute() {
				Saver.this.save();
			}
		}.begin();
	}

	@Override
	public Instance end() {
		this.save();
		return super.end();
	}

	private void save() {
		EditorContext.tryToSave(this.editor);
	}
}
