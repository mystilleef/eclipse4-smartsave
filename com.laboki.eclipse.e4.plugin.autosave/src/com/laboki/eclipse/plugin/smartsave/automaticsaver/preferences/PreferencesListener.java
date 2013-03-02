package com.laboki.eclipse.plugin.smartsave.automaticsaver.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import com.laboki.eclipse.plugin.smartsave.automaticsaver.ActivePart;

public final class PreferencesListener {

	private final IPreferenceChangeListener listener;
	private final IEclipsePreferences preferences = PreferencesStore.getPreferences();

	public PreferencesListener(final IPreferencesHandler handler) {
		this.listener = new ChangeListener(handler);
	}

	public void start() {
		this.preferences.addPreferenceChangeListener(this.listener);
	}

	public void stop() {
		this.preferences.removePreferenceChangeListener(this.listener);
	}

	private final class ChangeListener implements IPreferenceChangeListener, Runnable {

		private final IPreferencesHandler handler;

		public ChangeListener(final IPreferencesHandler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			this.handler.preferencesChanged();
		}

		@Override
		public void preferenceChange(final PreferenceChangeEvent event) {
			ActivePart.asyncExec(this);
		}
	}

	@Override
	public String toString() {
		return String.format("PreferencesListener [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
