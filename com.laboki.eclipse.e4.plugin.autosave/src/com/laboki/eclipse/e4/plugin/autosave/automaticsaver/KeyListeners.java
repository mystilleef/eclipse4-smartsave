package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Display;

final class KeyListeners implements KeyListener {

	private boolean isListening;
	private final KeyListenersHandler handler;
	private final StyledText editorBuffer = ActivePart.getBuffer();

	public KeyListeners(final KeyListenersHandler handler) {
		this.handler = handler;
	}

	void start() {
		if (this.isListening) return;
		this.editorBuffer.addKeyListener(this);
		this.isListening = true;
	}

	void stop() {
		if (!this.isListening) return;
		this.editorBuffer.removeKeyListener(this);
		this.isListening = false;
	}

	@Override
	public void keyPressed(final KeyEvent event) {
		Display.getDefault().asyncExec(this.new KeyPressRunnable());
	}

	@Override
	public void keyReleased(final KeyEvent event) {
		Display.getDefault().asyncExec(this.new KeyReleaseRunnable());
	}

	private final class KeyPressRunnable implements Runnable {

		public KeyPressRunnable() {}

		@Override
		public void run() {
			KeyListeners.this.getHandler().keyPress();
		}
	}

	final class KeyReleaseRunnable implements Runnable {

		public KeyReleaseRunnable() {}

		@Override
		public void run() {
			KeyListeners.this.getHandler().keyRelease();
		}
	}

	protected KeyListenersHandler getHandler() {
		return this.handler;
	}
}
