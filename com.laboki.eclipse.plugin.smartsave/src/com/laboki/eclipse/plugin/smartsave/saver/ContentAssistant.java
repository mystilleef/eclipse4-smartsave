package com.laboki.eclipse.plugin.smartsave.saver;

import com.laboki.eclipse.plugin.smartsave.saver.listeners.ISaverCompletionListener;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverCompletionListener;

final class ContentAssistant implements ISaverCompletionListener {

	private boolean isVisible;
	private final SaverCompletionListener listener = new SaverCompletionListener(this);

	public ContentAssistant() {
		this.listener.start();
	}

	@Override
	public void assistSessionStarted() {
		this.isVisible = true;
	}

	@Override
	public void assistSessionEnded() {
		this.isVisible = false;
	}

	public boolean isVisible() {
		return this.isVisible;
	}
}
