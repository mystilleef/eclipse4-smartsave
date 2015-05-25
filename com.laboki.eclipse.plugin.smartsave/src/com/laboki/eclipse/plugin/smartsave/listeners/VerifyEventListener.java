package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;

public final class VerifyEventListener extends BaseListener
	implements
		VerifyListener {

	private final Optional<StyledText> buffer = VerifyEventListener.getBuffer();

	private static Optional<StyledText>
	getBuffer() {
		return EditorContext.getBuffer(EditorContext.getEditor());
	}

	@Override
	public void
	add() {
		if (!this.buffer.isPresent()) return;
		this.buffer.get().addVerifyListener(this);
	}

	@Override
	public void
	remove() {
		if (!this.buffer.isPresent()) return;
		this.buffer.get().removeVerifyListener(this);
	}

	@Override
	public void
	verifyText(final VerifyEvent arg0) {
		BaseListener.scheduleSave();
	}
}
