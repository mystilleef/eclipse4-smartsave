package com.laboki.eclipse.plugin.smartsave.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

public final class PreferenceListener {

	private final IPreferenceChangeListener listener;
	private static final IEclipsePreferences PREFERENCES = PreferenceStore.getPreferences();

	public PreferenceListener(final IPreferenceHandler handler) {
		this.listener = new ChangeListener(handler);
	}

	public void start() {
		PreferenceListener.PREFERENCES.addPreferenceChangeListener(this.listener);
	}

	public void stop() {
		PreferenceListener.PREFERENCES.removePreferenceChangeListener(this.listener);
	}

	private final class ChangeListener implements IPreferenceChangeListener, Runnable {

		private final IPreferenceHandler handler;

		public ChangeListener(final IPreferenceHandler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			this.handler.preferencesChanged();
		}

		@Override
		public void preferenceChange(final PreferenceChangeEvent event) {
			EditorContext.asyncExec(this);
		}
	}
}
