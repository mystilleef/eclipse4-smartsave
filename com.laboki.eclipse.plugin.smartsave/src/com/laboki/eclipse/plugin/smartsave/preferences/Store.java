package com.laboki.eclipse.plugin.smartsave.preferences;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public enum Store {
	INSTANCE;

	private static final boolean CAN_SAVE_IF_WARNINGS_DEFAULT_VALUE = true;
	private static final boolean CAN_SAVE_IF_ERRORS_DEFAULT_VALUE = false;
	private static final boolean CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE = true;
	private static final int SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE = 3;
	private static final String SAVE_AUTOMATICALLY_KEY = "saveAutomatically";
	private static final String WARNINGS_KEY = "saveIfWarnings";
	private static final String ERRORS_KEY = "saveIfErrors";
	private static final String SAVE_INTERVAL_KEY = "saveIntervalInSeconds";
	private static final Logger LOGGER =
		Logger.getLogger(Store.class.getName());

	public static void setCanSaveAutomatically(final boolean saveAutomatically) {
		Store.setBoolean(Store.SAVE_AUTOMATICALLY_KEY, saveAutomatically);
	}

	public static boolean getCanSaveAutomatically() {
		return Store.getBoolean(Store.SAVE_AUTOMATICALLY_KEY,
			Store.CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE);
	}

	public static void toggleCanSaveAutomatically() {
		Store.setCanSaveAutomatically(!Store.getCanSaveAutomatically());
	}

	public static void setCanSaveIfWarnings(final boolean checkWarnings) {
		Store.setBoolean(Store.WARNINGS_KEY, checkWarnings);
	}

	public static boolean getCanSaveIfWarnings() {
		return Store.getBoolean(Store.WARNINGS_KEY,
			Store.CAN_SAVE_IF_WARNINGS_DEFAULT_VALUE);
	}

	public static void setCanSaveIfErrors(final boolean checkErrors) {
		Store.setBoolean(Store.ERRORS_KEY, checkErrors);
	}

	public static boolean getCanSaveIfErrors() {
		return Store.getBoolean(Store.ERRORS_KEY,
			Store.CAN_SAVE_IF_ERRORS_DEFAULT_VALUE);
	}

	public static
	void
	setSaveIntervalInSeconds(final int saveIntervalInSeconds) {
		Store.setInt(Store.SAVE_INTERVAL_KEY, saveIntervalInSeconds);
	}

	public static int getSaveIntervalInSeconds() {
		return Store.getInt(Store.SAVE_INTERVAL_KEY,
			Store.SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE);
	}

	private static void setBoolean(final String key, final boolean value) {
		final IEclipsePreferences pref = Store.getPreferences();
		pref.putBoolean(key, value);
		Store.update(pref);
	}

	private static boolean getBoolean(
		final String key,
		final boolean defaultValue) {
		final IEclipsePreferences pref = Store.getPreferences();
		Store.update(pref);
		return pref.getBoolean(key, defaultValue);
	}

	private static void setInt(final String key, final int value) {
		final IEclipsePreferences pref = Store.getPreferences();
		pref.putInt(key, value);
		Store.update(pref);
	}

	private static int getInt(final String key, final int defaultValue) {
		final IEclipsePreferences pref = Store.getPreferences();
		Store.update(pref);
		return pref.getInt(key, defaultValue);
	}

	public static void clear() {
		try {
			Store.tryToClear();
		}
		catch (final BackingStoreException e) {
			Store.LOGGER.log(Level.OFF, e.getMessage(), e);
		}
	}

	private static void tryToClear() throws BackingStoreException {
		final IEclipsePreferences pref = Store.getPreferences();
		pref.clear();
		Store.update(pref);
	}

	public static IEclipsePreferences getPreferences() {
		return ConfigurationScope.INSTANCE.getNode(EditorContext.PLUGIN_NAME);
	}

	private static void update(final IEclipsePreferences preferences) {
		try {
			Store.tryToUpdate(preferences);
		}
		catch (final BackingStoreException e) {
			Store.LOGGER.log(Level.OFF, e.getMessage(), e);
		}
	}

	private static void tryToUpdate(final IEclipsePreferences preferences)
	throws BackingStoreException {
		preferences.flush();
		preferences.sync();
	}
}
