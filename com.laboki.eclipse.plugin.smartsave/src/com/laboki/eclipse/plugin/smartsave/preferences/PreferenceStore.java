// $codepro.audit.disable debuggingCode
package com.laboki.eclipse.plugin.smartsave.preferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

public final class PreferenceStore {

	private static final boolean CAN_SAVE_IF_WARNINGS_DEFAULT_VALUE = true;
	private static final boolean CAN_SAVE_IF_ERRORS_DEFAULT_VALUE = false;
	private static final boolean CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE = true;
	private static final int SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE = 3;
	private static final String SAVE_AUTOMATICALLY_KEY = "saveAutomatically";
	private static final String WARNINGS_KEY = "saveIfWarnings";
	private static final String ERRORS_KEY = "saveIfErrors";
	private static final String SAVE_INTERVAL_KEY = "saveIntervalInSeconds";

	private PreferenceStore() {}

	public static void setCanSaveAutomatically(final boolean saveAutomatically) {
		PreferenceStore.setBoolean(PreferenceStore.SAVE_AUTOMATICALLY_KEY, saveAutomatically);
	}

	public static boolean getCanSaveAutomatically() {
		return PreferenceStore.getBoolean(PreferenceStore.SAVE_AUTOMATICALLY_KEY, PreferenceStore.CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE);
	}

	public static void setCanSaveIfWarnings(final boolean checkWarnings) {
		PreferenceStore.setBoolean(PreferenceStore.WARNINGS_KEY, checkWarnings);
	}

	public static boolean getCanSaveIfWarnings() {
		return PreferenceStore.getBoolean(PreferenceStore.WARNINGS_KEY, PreferenceStore.CAN_SAVE_IF_WARNINGS_DEFAULT_VALUE);
	}

	public static void setCanSaveIfErrors(final boolean checkErrors) {
		PreferenceStore.setBoolean(PreferenceStore.ERRORS_KEY, checkErrors);
	}

	public static boolean getCanSaveIfErrors() {
		return PreferenceStore.getBoolean(PreferenceStore.ERRORS_KEY, PreferenceStore.CAN_SAVE_IF_ERRORS_DEFAULT_VALUE);
	}

	public static void setSaveIntervalInSeconds(final int saveIntervalInSeconds) {
		PreferenceStore.setInt(PreferenceStore.SAVE_INTERVAL_KEY, saveIntervalInSeconds);
	}

	public static int getSaveIntervalInSeconds() {
		return PreferenceStore.getInt(PreferenceStore.SAVE_INTERVAL_KEY, PreferenceStore.SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE);
	}

	public static void clear() {
		try {
			final IEclipsePreferences pref = PreferenceStore.getPreferences();
			pref.clear();
			PreferenceStore.sync(pref);
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static IEclipsePreferences getPreferences() {
		return ConfigurationScope.INSTANCE.getNode(EditorContext.PLUGIN_NAME);
	}

	private static void setBoolean(final String key, final boolean value) {
		final IEclipsePreferences pref = PreferenceStore.getPreferences();
		pref.putBoolean(key, value);
		PreferenceStore.sync(pref);
	}

	private static boolean getBoolean(final String key, final boolean defaultValue) {
		final IEclipsePreferences pref = PreferenceStore.getPreferences();
		PreferenceStore.sync(pref);
		return pref.getBoolean(key, defaultValue);
	}

	private static void setInt(final String key, final int value) {
		final IEclipsePreferences pref = PreferenceStore.getPreferences();
		pref.putInt(key, value);
		PreferenceStore.sync(pref);
	}

	private static int getInt(final String key, final int defaultValue) {
		final IEclipsePreferences pref = PreferenceStore.getPreferences();
		PreferenceStore.sync(pref);
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
