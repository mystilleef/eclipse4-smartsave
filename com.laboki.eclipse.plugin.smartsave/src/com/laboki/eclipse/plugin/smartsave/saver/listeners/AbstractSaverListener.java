package com.laboki.eclipse.plugin.smartsave.saver.listeners;

abstract class AbstractSaverListener implements ISaverListener {

	private boolean isListening;

	protected AbstractSaverListener() {}

	@Override
	public void start() {
		if (this.isListening) return;
		this.add();
		this.isListening = true;
	}

	@Override
	public void stop() {
		if (!this.isListening) return;
		this.remove();
		this.isListening = false;
	}

	protected void add() {}

	protected void remove() {}
}
