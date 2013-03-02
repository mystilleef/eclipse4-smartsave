package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences;

public final class Preferences implements IPreferencesHandler {

	private static Preferences instance;
	private static int saveIntervalInSeconds = PreferencesStore.getSaveIntervalInSeconds();
	private static boolean canSaveAutomatically = PreferencesStore.getCanSaveAutomatically();
	private static boolean canSaveIfErrors = PreferencesStore.getCanSaveIfErrors();
	private static boolean canSaveIfWarnings = PreferencesStore.getCanSaveIfWarnings();
	private final PreferencesListener listener = new PreferencesListener(this);

	private Preferences() {
		this.listener.start();
	}

	public static synchronized Preferences initialize() {
		if (Preferences.instance == null) Preferences.instance = new Preferences();
		return Preferences.instance;
	}

	public static int saveIntervalInSeconds() {
		return Preferences.saveIntervalInSeconds;
	}

	public static boolean canSaveAutomatically() {
		return Preferences.canSaveAutomatically;
	}

	public static boolean canSaveIfErrors() {
		return Preferences.canSaveIfErrors;
	}

	public static boolean canSaveIfWarnings() {
		return Preferences.canSaveIfWarnings;
	}

	@Override
	public void preferencesChanged() {
		Preferences.saveIntervalInSeconds = PreferencesStore.getSaveIntervalInSeconds();
		Preferences.canSaveAutomatically = PreferencesStore.getCanSaveAutomatically();
		Preferences.canSaveIfErrors = PreferencesStore.getCanSaveIfErrors();
		Preferences.canSaveIfWarnings = PreferencesStore.getCanSaveIfWarnings();
	}

	@Override
	public String toString() {
		return String.format("Preferences [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
