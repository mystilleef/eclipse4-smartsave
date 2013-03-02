package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

final class AutosaveFocusListener implements FocusListener {

	private boolean isListening;
	private final IAutosaveFocusListenerHandler handler;
	private final FocusGainedRunnable focusGainedRunnable = new FocusGainedRunnable();
	private final FocusLostRunnable focusLostRunnable = new FocusLostRunnable();
	private final StyledText editorBuffer = ActivePart.getBuffer();

	public AutosaveFocusListener(final IAutosaveFocusListenerHandler handler) {
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
		ActivePart.asyncExec(this.focusGainedRunnable);
	}

	@Override
	public void focusLost(final FocusEvent event) {
		ActivePart.asyncExec(this.focusLostRunnable);
	}

	private final class FocusGainedRunnable implements Runnable {

		public FocusGainedRunnable() {}

		@Override
		public void run() {
			AutosaveFocusListener.this.getHandler().focusGained();
		}
	}

	private final class FocusLostRunnable implements Runnable {

		public FocusLostRunnable() {}

		@Override
		public void run() {
			AutosaveFocusListener.this.getHandler().focusLost();
		}
	}

	public IAutosaveFocusListenerHandler getHandler() {
		return this.handler;
	}

	@Override
	public String toString() {
		return String.format("AutosaveFocusListener [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
