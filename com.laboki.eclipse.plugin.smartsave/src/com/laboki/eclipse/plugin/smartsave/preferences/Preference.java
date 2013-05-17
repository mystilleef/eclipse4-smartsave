package com.laboki.eclipse.plugin.smartsave.preferences;

public enum Preference {
	INSTANCE;

	private int saveIntervalInSeconds = PreferenceStore.getSaveIntervalInSeconds();
	private boolean canSaveAutomatically = PreferenceStore.getCanSaveAutomatically();
	private boolean canSaveIfErrors = PreferenceStore.getCanSaveIfErrors();
	private boolean canSaveIfWarnings = PreferenceStore.getCanSaveIfWarnings();

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
		this.saveIntervalInSeconds = PreferenceStore.getSaveIntervalInSeconds();
		this.canSaveAutomatically = PreferenceStore.getCanSaveAutomatically();
		this.canSaveIfErrors = PreferenceStore.getCanSaveIfErrors();
		this.canSaveIfWarnings = PreferenceStore.getCanSaveIfWarnings();
	}
}
