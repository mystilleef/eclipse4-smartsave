package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.laboki.eclipse.plugin.smartsave.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public final class VerifyEventListener extends AbstractListener implements
	VerifyListener {

	private final StyledText buffer =
		EditorContext.getBuffer(EditorContext
												.getEditor());

	public VerifyEventListener() {
		super();
	}

	@Override
	public void add() {
		if (this.buffer == null) return;
		this.buffer.addVerifyListener(this);
	}

	@Override
	public void remove() {
		if (this.buffer == null) return;
		this.buffer.removeVerifyListener(this);
	}

	@Override
	public void verifyText(final VerifyEvent arg0) {
		AbstractListener.scheduleSave();
	}
}
