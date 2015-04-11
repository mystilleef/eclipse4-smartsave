package com.laboki.eclipse.plugin.smartsave.preferences;

public enum Cache {
	INSTANCE;

	private int saveIntervalInSeconds = Store.getSaveIntervalInSeconds();
	private boolean canSaveAutomatically = Store.getCanSaveAutomatically();
	private boolean canSaveIfErrors = Store.getCanSaveIfErrors();
	private boolean canSaveIfWarnings = Store.getCanSaveIfWarnings();

	public int saveIntervalInSeconds() {
		return this.saveIntervalInSeconds;
	}

	public boolean canSaveAutomatically() {
		return this.canSaveAutomatically;
	}

	public boolean canSaveIfErrors() {
		return this.canSaveIfErrors;
	}

	public boolean canSaveIfWarnings() {
		return this.canSaveIfWarnings;
	}

	public synchronized void update() {
		this.saveIntervalInSeconds = Store.getSaveIntervalInSeconds();
		this.canSaveAutomatically = Store.getCanSaveAutomatically();
		this.canSaveIfErrors = Store.getCanSaveIfErrors();
		this.canSaveIfWarnings = Store.getCanSaveIfWarnings();
	}
}
