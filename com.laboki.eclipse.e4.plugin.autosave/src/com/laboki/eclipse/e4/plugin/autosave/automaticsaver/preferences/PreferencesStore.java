package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.laboki.eclipse.e4.plugin.autosave.AddonMetadata;

public final class PreferencesStore {

	private static final boolean CAN_CHECK_WARNINGS_DEFAULT_VALUE = false;
	private static final boolean CAN_CHECK_ERRORS_DEFAULT_VALUE = true;
	private static final boolean CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE = true;
	private static final int SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE = 5;
	private static final String SAVE_AUTOMATICALLY_KEY = "saveAutomatically";
	private static final String CHECK_WARNINGS_KEY = "checkWarnings";
	private static final String CHECK_ERRORS_KEY = "checkErrors";
	private static final String SAVE_INTERVAL_KEY = "saveIntervalInSeconds";
	private static final IEclipsePreferences PREFERENCES = InstanceScope.INSTANCE.getNode(AddonMetadata.PLUGIN_NAME);

	private PreferencesStore() {}

	public static void setCanSaveAutomatically(final boolean saveAutomatically) {
		PreferencesStore.PREFERENCES.putBoolean(PreferencesStore.SAVE_AUTOMATICALLY_KEY, saveAutomatically);
		PreferencesStore.flush();
	}

	public static boolean getCanSaveAutomatically() {
		PreferencesStore.sync();
		return PreferencesStore.PREFERENCES.getBoolean(PreferencesStore.SAVE_AUTOMATICALLY_KEY, PreferencesStore.CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE);
	}

	public static void setCanCheckWarnings(final boolean checkWarnings) {
		PreferencesStore.PREFERENCES.putBoolean(PreferencesStore.CHECK_WARNINGS_KEY, checkWarnings);
		PreferencesStore.flush();
	}

	public static boolean getCanCheckWarnings() {
		PreferencesStore.sync();
		return PreferencesStore.PREFERENCES.getBoolean(PreferencesStore.CHECK_WARNINGS_KEY, PreferencesStore.CAN_CHECK_WARNINGS_DEFAULT_VALUE);
	}

	public static void setCanCheckErrors(final boolean checkErrors) {
		PreferencesStore.PREFERENCES.putBoolean(PreferencesStore.CHECK_ERRORS_KEY, checkErrors);
		PreferencesStore.flush();
	}

	public static boolean getCanCheckErrors() {
		PreferencesStore.sync();
		return PreferencesStore.PREFERENCES.getBoolean(PreferencesStore.CHECK_ERRORS_KEY, PreferencesStore.CAN_CHECK_ERRORS_DEFAULT_VALUE);
	}

	public static void setSaveIntervalInSeconds(final int saveIntervalInSeconds) {
		PreferencesStore.PREFERENCES.putInt(PreferencesStore.SAVE_INTERVAL_KEY, saveIntervalInSeconds);
		PreferencesStore.flush();
	}

	public static int getSaveIntervalInSeconds() {
		PreferencesStore.sync();
		return PreferencesStore.PREFERENCES.getInt(PreferencesStore.SAVE_INTERVAL_KEY, PreferencesStore.SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE);
	}

	private static void flush() {
		try {
			PreferencesStore.PREFERENCES.flush();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private static void sync() {
		try {
			PreferencesStore.PREFERENCES.sync();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
