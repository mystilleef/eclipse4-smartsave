package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

@ToString
public final class SaverFocusListener extends AbstractSaverListener implements FocusListener {

	@Getter private final ISaverFocusListenerHandler handler;
	private final FocusGainedRunnable focusGainedRunnable = new FocusGainedRunnable();
	private final FocusLostRunnable focusLostRunnable = new FocusLostRunnable();
	private final StyledText editorBuffer = EditorContext.getBuffer(EditorContext.getEditor());

	public SaverFocusListener(final ISaverFocusListenerHandler handler) {
		this.handler = handler;
	}

	@Override
	public void add() {
		this.editorBuffer.addFocusListener(this);
	}

	@Override
	public void remove() {
		this.editorBuffer.removeFocusListener(this);
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
}
