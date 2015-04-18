package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public final class VerifyEventListener extends AbstractListener
	implements
		VerifyListener {

	private final Optional<StyledText> buffer =
		Optional.fromNullable(EditorContext.getBuffer(EditorContext.getEditor()));

	public VerifyEventListener() {
		super();
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
		AbstractListener.scheduleSave();
	}
}
