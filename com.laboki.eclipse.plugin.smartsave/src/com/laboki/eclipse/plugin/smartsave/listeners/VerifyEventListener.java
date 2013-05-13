package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.laboki.eclipse.plugin.smartsave.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;

public final class VerifyEventListener extends AbstractListener implements VerifyListener {

	private final StyledText buffer = EditorContext.getBuffer(EditorContext.getEditor());

	public VerifyEventListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public void add() {
		this.buffer.addVerifyListener(this);
	}

	@Override
	public void remove() {
		this.buffer.removeVerifyListener(this);
	}

	@Override
	public void verifyText(final VerifyEvent arg0) {
		this.scheduleSave();
	}
}
