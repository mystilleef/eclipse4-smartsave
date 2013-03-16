package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

@ToString
public final class SaverKeyListener extends AbstractSaverListener implements KeyListener {

	@Getter private final ISaverKeyListenerHandler handler;
	private final StyledText editorBuffer = EditorContext.getBuffer();
	private final KeyPressRunnable keyPressRunnable = new KeyPressRunnable();
	private final KeyReleaseRunnable keyReleaseRunnable = new KeyReleaseRunnable();

	public SaverKeyListener(final ISaverKeyListenerHandler handler) {
		this.handler = handler;
	}

	@Override
	public void add() {
		this.editorBuffer.addKeyListener(this);
	}

	@Override
	public void remove() {
		this.editorBuffer.removeKeyListener(this);
	}

	@Override
	public void keyPressed(final KeyEvent event) {
		EditorContext.asyncExec(this.keyPressRunnable);
	}

	@Override
	public void keyReleased(final KeyEvent event) {
		EditorContext.asyncExec(this.keyReleaseRunnable);
	}

	private final class KeyPressRunnable implements Runnable {

		public KeyPressRunnable() {}

		@Override
		public void run() {
			SaverKeyListener.this.getHandler().keyPress();
		}
	}

	private final class KeyReleaseRunnable implements Runnable {

		public KeyReleaseRunnable() {}

		@Override
		public void run() {
			SaverKeyListener.this.getHandler().keyRelease();
		}
	}
}
