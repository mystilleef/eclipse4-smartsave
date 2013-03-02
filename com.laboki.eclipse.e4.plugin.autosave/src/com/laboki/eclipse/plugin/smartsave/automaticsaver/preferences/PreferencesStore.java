// $codepro.audit.disable debuggingCode
package com.laboki.eclipse.plugin.smartsave.automaticsaver.preferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.laboki.eclipse.plugin.smartsave.AddonMetadata;

public final class PreferencesStore {

	private static final boolean CAN_SAVE_IF_WARNINGS_DEFAULT_VALUE = true;
	private static final boolean CAN_SAVE_IF_ERRORS_DEFAULT_VALUE = false;
	private static final boolean CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE = true;
	private static final int SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE = 3;
	private static final String SAVE_AUTOMATICALLY_KEY = "saveAutomatically";
	private static final String WARNINGS_KEY = "saveIfWarnings";
	private static final String ERRORS_KEY = "saveIfErrors";
	private static final String SAVE_INTERVAL_KEY = "saveIntervalInSeconds";

	private PreferencesStore() {}

	public static void setCanSaveAutomatically(final boolean saveAutomatically) {
		PreferencesStore.setBoolean(PreferencesStore.SAVE_AUTOMATICALLY_KEY, saveAutomatically);
	}

	public static boolean getCanSaveAutomatically() {
		return PreferencesStore.getBoolean(PreferencesStore.SAVE_AUTOMATICALLY_KEY, PreferencesStore.CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE);
	}

	public static void setCanSaveIfWarnings(final boolean checkWarnings) {
		PreferencesStore.setBoolean(PreferencesStore.WARNINGS_KEY, checkWarnings);
	}

	public static boolean getCanSaveIfWarnings() {
		return PreferencesStore.getBoolean(PreferencesStore.WARNINGS_KEY, PreferencesStore.CAN_SAVE_IF_WARNINGS_DEFAULT_VALUE);
	}

	public static void setCanSaveIfErrors(final boolean checkErrors) {
		PreferencesStore.setBoolean(PreferencesStore.ERRORS_KEY, checkErrors);
	}

	public static boolean getCanSaveIfErrors() {
		return PreferencesStore.getBoolean(PreferencesStore.ERRORS_KEY, PreferencesStore.CAN_SAVE_IF_ERRORS_DEFAULT_VALUE);
	}

	public static void setSaveIntervalInSeconds(final int saveIntervalInSeconds) {
		PreferencesStore.setInt(PreferencesStore.SAVE_INTERVAL_KEY, saveIntervalInSeconds);
	}

	public static int getSaveIntervalInSeconds() {
		return PreferencesStore.getInt(PreferencesStore.SAVE_INTERVAL_KEY, PreferencesStore.SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE);
	}

	public static void clear() {
		try {
			final IEclipsePreferences pref = PreferencesStore.getPreferences();
			pref.clear();
			PreferencesStore.sync(pref);
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static IEclipsePreferences getPreferences() {
		return ConfigurationScope.INSTANCE.getNode(AddonMetadata.PLUGIN_NAME);
	}

	private static void setBoolean(final String key, final boolean value) {
		final IEclipsePreferences pref = PreferencesStore.getPreferences();
		pref.putBoolean(key, value);
		PreferencesStore.sync(pref);
	}

	private static boolean getBoolean(final String key, final boolean defaultValue) {
		final IEclipsePreferences pref = PreferencesStore.getPreferences();
		PreferencesStore.sync(pref);
		return pref.getBoolean(key, defaultValue);
	}

	private static void setInt(final String key, final int value) {
		final IEclipsePreferences pref = PreferencesStore.getPreferences();
		pref.putInt(key, value);
		PreferencesStore.sync(pref);
	}

	private static int getInt(final String key, final int defaultValue) {
		final IEclipsePreferences pref = PreferencesStore.getPreferences();
		PreferencesStore.sync(pref);
		return pref.getInt(key, defaultValue);
	}

	private static void sync(final IEclipsePreferences preferences) {
		try {
			preferences.flush();
			preferences.sync();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
