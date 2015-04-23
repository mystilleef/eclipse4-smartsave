package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.events.PartChangedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;

public final class DirtyPartListener extends AbstractEventBusInstance
	implements
		IPropertyListener {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	public DirtyPartListener() {
		super();
	}

	public void
	add() {
		if (!this.editor.isPresent()) return;
		this.editor.get().addPropertyListener(this);
	}

	public void
	remove() {
		if (!this.editor.isPresent()) return;
		this.editor.get().removePropertyListener(this);
	}

	@Override
	public void
	propertyChanged(final Object source, final int propID) {
		if (propID == IEditorPart.PROP_DIRTY) DirtyPartListener.postEvent();
	}

	@Override
	public Instance
	start() {
		this.add();
		DirtyPartListener.postEvent();
		return super.start();
	}

	private static void
	postEvent() {
		EventBus.post(new PartChangedEvent());
	}

	@Override
	public Instance
	stop() {
		this.remove();
		return super.stop();
	}
}
