package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.listeners.abstraction.BaseListener;

public final class KeyEventListener extends BaseListener
	implements
		KeyListener {

	private final Optional<Control> control = KeyEventListener.getControl();

	private static Optional<Control>
	getControl() {
		final Optional<IEditorPart> editor = EditorContext.getEditor();
		if (!editor.isPresent()) return Optional.absent();
		return EditorContext.getControl(editor);
	}

	@Override
	public void
	add() {
		if (!this.control.isPresent()) return;
		this.control.get().addKeyListener(this);
	}

	@Override
	public void
	remove() {
		if (!this.control.isPresent()) return;
		this.control.get().removeKeyListener(this);
	}

	@Override
	public void
	keyPressed(final KeyEvent event) {
		EditorContext.cancelSaverTasks();
	}

	@Override
	public void
	keyReleased(final KeyEvent event) {
		BaseListener.scheduleSave();
	}
}
