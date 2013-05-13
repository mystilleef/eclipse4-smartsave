package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;

public final class SaverKeyEventListener extends AbstractSaverListener implements KeyListener {

	private final Control control = EditorContext.getControl(EditorContext.getEditor());

	public SaverKeyEventListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public void add() {
		this.control.addKeyListener(this);
	}

	@Override
	public void remove() {
		this.control.removeKeyListener(this);
	}

	@Override
	public void keyPressed(final KeyEvent event) {
		this.scheduleSave();
	}

	@Override
	public void keyReleased(final KeyEvent event) {
		this.scheduleSave();
	}
}
