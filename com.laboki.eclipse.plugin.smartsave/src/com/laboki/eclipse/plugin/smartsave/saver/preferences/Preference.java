package com.laboki.eclipse.plugin.smartsave.saver.preferences;

public final class Preference implements IPreferenceHandler {

	private static Preference instance;
	private int saveIntervalInSeconds = PreferenceStore.getSaveIntervalInSeconds();
	private boolean canSaveAutomatically = PreferenceStore.getCanSaveAutomatically();
	private boolean canSaveIfErrors = PreferenceStore.getCanSaveIfErrors();
	private boolean canSaveIfWarnings = PreferenceStore.getCanSaveIfWarnings();
	private final PreferenceListener listener = new PreferenceListener(this);

	private Preference() {
		this.listener.start();
	}

	public static synchronized Preference instance() {
		if (Preference.instance == null) Preference.instance = new Preference();
		return Preference.instance;
	}

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

	@Override
	public void preferencesChanged() {
		this.saveIntervalInSeconds = PreferenceStore.getSaveIntervalInSeconds();
		this.canSaveAutomatically = PreferenceStore.getCanSaveAutomatically();
		this.canSaveIfErrors = PreferenceStore.getCanSaveIfErrors();
		this.canSaveIfWarnings = PreferenceStore.getCanSaveIfWarnings();
	}

	@Override
	public String toString() {
		return String.format("Preference [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
