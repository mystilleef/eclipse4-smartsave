package com.laboki.eclipse.plugin.smartsave.saver;

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
