package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

import com.laboki.eclipse.plugin.smartsave.events.PartChangedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;

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
		if (propID == IEditorPart.PROP_DIRTY) DirtyPartListener.this.postEvent();
	}

	@Override
	public Instance begin() {
		this.add();
		this.postEvent();
		return super.begin();
	}

	private void postEvent() {
		DirtyPartListener.this.eventBus.post(new PartChangedEvent());
	}

	@Override
	public Instance end() {
		this.remove();
		return super.end();
	}
}
