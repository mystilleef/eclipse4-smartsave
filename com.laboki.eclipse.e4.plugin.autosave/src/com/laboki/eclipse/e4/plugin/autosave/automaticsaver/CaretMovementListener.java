package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

public final class CaretMovementListener implements CaretListener {

	private final ListenerHandler handler;
	private boolean isListening;
	private final StyledText editorBuffer = ActivePart.getBuffer();

	CaretMovementListener(final ListenerHandler handler) {
		this.handler = handler;
	}

	void start() {
		if (this.isListening) return;
		this.editorBuffer.addCaretListener(this);
		this.isListening = true;
	}

	void stop() {
		if (!this.isListening) return;
		this.editorBuffer.removeCaretListener(this);
		this.isListening = false;
	}

	protected ListenerHandler getHandler() {
		return this.handler;
	}

	@Override
	public void caretMoved(final CaretEvent event) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				CaretMovementListener.this.getHandler().handle(event);
			}
		});
	}
}
