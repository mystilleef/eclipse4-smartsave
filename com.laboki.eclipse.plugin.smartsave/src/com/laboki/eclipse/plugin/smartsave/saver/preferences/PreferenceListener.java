package com.laboki.eclipse.plugin.smartsave.saver.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

public final class PreferenceListener {

	private final IPreferenceChangeListener listener;
	private final IEclipsePreferences preferences = PreferenceStore.getPreferences();

	public PreferenceListener(final IPreferenceHandler handler) {
		this.listener = new ChangeListener(handler);
	}

	public void start() {
		this.preferences.addPreferenceChangeListener(this.listener);
	}

	public void stop() {
		this.preferences.removePreferenceChangeListener(this.listener);
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

	@Override
	public String toString() {
		return String.format("PreferenceListener [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
