package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

public final class PreferencesListener {

	private final IPreferenceChangeListener listener;

	public PreferencesListener(final IPreferencesHandler handler) {
		this.listener = new ChangeListener(handler);
	}

	public void start() {
		PreferencesStore.PREFERENCES.addPreferenceChangeListener(this.listener);
	}

	public void stop() {
		PreferencesStore.PREFERENCES.removePreferenceChangeListener(this.listener);
	}

	private final class ChangeListener implements IPreferenceChangeListener {

		private final IPreferencesHandler handler;

		public ChangeListener(final IPreferencesHandler handler) {
			this.handler = handler;
		}

		@Override
		public void preferenceChange(final PreferenceChangeEvent event) {
			this.handler.preferencesChanged();
		}
	}
}
