package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

import com.laboki.eclipse.plugin.smartsave.events.PartChangedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class DirtyPartListener extends AbstractEventBusInstance implements IPropertyListener {

	private final IEditorPart editor = EditorContext.getEditor();

	public DirtyPartListener(final EventBus eventBus) {
		super(eventBus);
	}

	public void add() {
		this.editor.addPropertyListener(this);
	}

	public void remove() {
		this.editor.removePropertyListener(this);
	}

	@Override
	public void propertyChanged(final Object source, final int propID) {
		if (propID == IEditorPart.PROP_DIRTY) new Task() {

			@Override
			public void execute() {
				DirtyPartListener.this.eventBus.post(new PartChangedEvent());
			}
		}.begin();
	}

	@Override
	public Instance begin() {
		this.add();
		return super.begin();
	}

	@Override
	public Instance end() {
		this.remove();
		return super.end();
	}
}
