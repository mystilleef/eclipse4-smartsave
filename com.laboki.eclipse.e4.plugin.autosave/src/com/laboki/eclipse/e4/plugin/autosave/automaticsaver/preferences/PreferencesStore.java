package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences;

public final class PreferencesStore {

	private static final boolean CAN_CHECK_WARNINGS_DEFAULT_VALUE = false;
	private static final boolean CAN_CHECK_ERRORS_DEFAULT_VALUE = true;
	private static final boolean CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE = true;
	private static final int SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE = 5;

	private PreferencesStore() {}

	public static void setCanSaveAutomatically(@SuppressWarnings("unused") final boolean saveAutomatically) {}

	public static boolean getCanSaveAutomatically() {
		return PreferencesStore.CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE;
	}

	public static void setCanCheckWarnings(@SuppressWarnings("unused") final boolean checkWarnings) {}

	public static boolean getCanCheckWarnings() {
		return PreferencesStore.CAN_CHECK_WARNINGS_DEFAULT_VALUE;
	}

	public static void setCanCheckErrors(@SuppressWarnings("unused") final boolean checkErrors) {}

	public static boolean getCanCheckErrors() {
		return PreferencesStore.CAN_CHECK_ERRORS_DEFAULT_VALUE;
	}

	public static void setSaveIntervalInSeconds(@SuppressWarnings("unused") final int time) {}

	public static int getSaveIntervalInSeconds() {
		return PreferencesStore.SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE;
	}
}
