package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import lombok.ToString;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.Task;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;
import com.laboki.eclipse.plugin.smartsave.saver.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.EnableSaveListenersEvent;

@ToString
public final class DirtyPartListener implements IPropertyListener, Instance {

	private final IEditorPart editor = EditorContext.getEditor();
	private final EventBus eventBus;

	public DirtyPartListener(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void add() {
		this.editor.addPropertyListener(this);
	}

	public void remove() {
		this.editor.removePropertyListener(this);
	}

	@Override
	public void propertyChanged(final Object source, final int propID) {
		if (propID == IEditorPart.PROP_DIRTY) EditorContext.asyncExec(new Task(EditorContext.SCHEDULED_SAVER_TASK) {

			@Override
			public void asyncExec() {
				if (DirtyPartListener.this.editor.isDirty()) DirtyPartListener.this.eventBus.post(new EnableSaveListenersEvent());
				else DirtyPartListener.this.eventBus.post(new DisableSaveListenersEvent());
			}
		});
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		this.add();
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		this.remove();
		return this;
	}
}
