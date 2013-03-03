package com.laboki.eclipse.plugin.smartsave.saver.preferences;

public final class Preference implements IPreferenceHandler {

	private static Preference instance;
	private static int saveIntervalInSeconds = PreferenceStore.getSaveIntervalInSeconds();
	private static boolean canSaveAutomatically = PreferenceStore.getCanSaveAutomatically();
	private static boolean canSaveIfErrors = PreferenceStore.getCanSaveIfErrors();
	private static boolean canSaveIfWarnings = PreferenceStore.getCanSaveIfWarnings();
	private final PreferenceListener listener = new PreferenceListener(this);

	private Preference() {
		this.listener.start();
	}

	public static synchronized Preference initialize() {
		if (Preference.instance == null) Preference.instance = new Preference();
		return Preference.instance;
	}

	public static int saveIntervalInSeconds() {
		return Preference.saveIntervalInSeconds;
	}

	public static boolean canSaveAutomatically() {
		return Preference.canSaveAutomatically;
	}

	public static boolean canSaveIfErrors() {
		return Preference.canSaveIfErrors;
	}

	public static boolean canSaveIfWarnings() {
		return Preference.canSaveIfWarnings;
	}

	@Override
	public void preferencesChanged() {
		Preference.saveIntervalInSeconds = PreferenceStore.getSaveIntervalInSeconds();
		Preference.canSaveAutomatically = PreferenceStore.getCanSaveAutomatically();
		Preference.canSaveIfErrors = PreferenceStore.getCanSaveIfErrors();
		Preference.canSaveIfWarnings = PreferenceStore.getCanSaveIfWarnings();
	}

	@Override
	public String toString() {
		return String.format("Preference [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
