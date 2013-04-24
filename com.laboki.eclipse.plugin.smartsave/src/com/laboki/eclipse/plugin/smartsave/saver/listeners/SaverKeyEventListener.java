package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;

public final class SaverKeyEventListener extends AbstractSaverListener implements KeyListener {

	private final StyledText buffer = EditorContext.getBuffer(EditorContext.getEditor());

	public SaverKeyEventListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public void add() {
		this.buffer.addKeyListener(this);
	}

	@Override
	public void remove() {
		this.buffer.removeKeyListener(this);
	};

	@Override
	public void keyPressed(final KeyEvent arg0) {
		this.scheduleSave();
	}

	@Override
	public void keyReleased(final KeyEvent arg0) {
		this.scheduleSave();
	}
}
