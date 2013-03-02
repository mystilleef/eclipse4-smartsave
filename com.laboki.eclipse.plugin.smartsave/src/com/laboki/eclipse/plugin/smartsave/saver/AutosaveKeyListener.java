package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

final class AutosaveKeyListener implements KeyListener {

	private boolean isListening;
	private final IAutosaveKeyListenersHandler handler;
	private final StyledText editorBuffer = ActivePart.getBuffer();
	private final KeyPressRunnable keyPressRunnable = new KeyPressRunnable();
	private final KeyReleaseRunnable keyReleaseRunnable = new KeyReleaseRunnable();

	public AutosaveKeyListener(final IAutosaveKeyListenersHandler handler) {
		this.handler = handler;
	}

	public void start() {
		if (this.isListening) return;
		this.editorBuffer.addKeyListener(this);
		this.isListening = true;
	}

	public void stop() {
		if (!this.isListening) return;
		this.editorBuffer.removeKeyListener(this);
		this.isListening = false;
	}

	@Override
	public void keyPressed(final KeyEvent event) {
		ActivePart.asyncExec(this.keyPressRunnable);
	}

	@Override
	public void keyReleased(final KeyEvent event) {
		ActivePart.asyncExec(this.keyReleaseRunnable);
	}

	private final class KeyPressRunnable implements Runnable {

		public KeyPressRunnable() {}

		@Override
		public void run() {
			AutosaveKeyListener.this.getHandler().keyPress();
		}
	}

	private final class KeyReleaseRunnable implements Runnable {

		public KeyReleaseRunnable() {}

		@Override
		public void run() {
			AutosaveKeyListener.this.getHandler().keyRelease();
		}
	}

	protected IAutosaveKeyListenersHandler getHandler() {
		return this.handler;
	}

	@Override
	public String toString() {
		return String.format("AutosaveKeyListener [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
