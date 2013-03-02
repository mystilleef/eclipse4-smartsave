package com.laboki.eclipse.plugin.smartsave.saver.preferences;

public final class Preference implements IPreferencesHandler {

	private static Preference instance;
	private static int saveIntervalInSeconds = PreferencesStore.getSaveIntervalInSeconds();
	private static boolean canSaveAutomatically = PreferencesStore.getCanSaveAutomatically();
	private static boolean canSaveIfErrors = PreferencesStore.getCanSaveIfErrors();
	private static boolean canSaveIfWarnings = PreferencesStore.getCanSaveIfWarnings();
	private final PreferencesListener listener = new PreferencesListener(this);

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
		Preference.saveIntervalInSeconds = PreferencesStore.getSaveIntervalInSeconds();
		Preference.canSaveAutomatically = PreferencesStore.getCanSaveAutomatically();
		Preference.canSaveIfErrors = PreferencesStore.getCanSaveIfErrors();
		Preference.canSaveIfWarnings = PreferencesStore.getCanSaveIfWarnings();
	}

	@Override
	public String toString() {
		return String.format("Preference [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
