package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

public final class SaverFocusListener implements FocusListener {

	private boolean isListening;
	private final ISaverFocusListenerHandler handler;
	private final FocusGainedRunnable focusGainedRunnable = new FocusGainedRunnable();
	private final FocusLostRunnable focusLostRunnable = new FocusLostRunnable();
	private final StyledText editorBuffer = EditorContext.getBuffer();

	public SaverFocusListener(final ISaverFocusListenerHandler handler) {
		this.handler = handler;
	}

	public void start() {
		if (this.isListening) return;
		this.editorBuffer.addFocusListener(this);
		this.isListening = true;
	}

	public void stop() {
		if (!this.isListening) return;
		this.editorBuffer.removeFocusListener(this);
		this.isListening = false;
	}

	@Override
	public void focusGained(final FocusEvent event) {
		EditorContext.asyncExec(this.focusGainedRunnable);
	}

	@Override
	public void focusLost(final FocusEvent event) {
		EditorContext.asyncExec(this.focusLostRunnable);
	}

	private final class FocusGainedRunnable implements Runnable {

		public FocusGainedRunnable() {}

		@Override
		public void run() {
			SaverFocusListener.this.getHandler().focusGained();
		}
	}

	private final class FocusLostRunnable implements Runnable {

		public FocusLostRunnable() {}

		@Override
		public void run() {
			SaverFocusListener.this.getHandler().focusLost();
		}
	}

	public ISaverFocusListenerHandler getHandler() {
		return this.handler;
	}

	@Override
	public String toString() {
		return String.format("SaverFocusListener [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
