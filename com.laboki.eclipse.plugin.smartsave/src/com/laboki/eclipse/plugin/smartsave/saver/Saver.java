package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.Task;
import com.laboki.eclipse.plugin.smartsave.saver.events.StartSaveScheduleEvent;

public final class Saver implements Instance {

	private final EventBus eventBus;
	private final IEditorPart editor = EditorContext.getEditor();

	public Saver(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void save(@SuppressWarnings("unused") final StartSaveScheduleEvent event) {
		EditorContext.asyncExec(new Task(EditorContext.AUTOMATIC_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			protected void asyncExec() {
				Saver.this.save();
			}
		});
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		this.save();
		return this;
	}

	private void save() {
		EditorContext.tryToSave(this.editor);
	}
}
