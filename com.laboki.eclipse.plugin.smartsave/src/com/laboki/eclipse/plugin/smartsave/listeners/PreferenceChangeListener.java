package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class PreferenceChangeListener extends AbstractEventBusInstance implements IPreferenceChangeListener {

	private static final IEclipsePreferences PREFERENCES = Store.getPreferences();

	public PreferenceChangeListener(final EventBus eventBus) {
		super(eventBus);
	}

	@Override
	public void preferenceChange(final PreferenceChangeEvent arg0) {
		new Task() {

			@Override
			public void execute() {
				PreferenceChangeListener.this.eventBus.post(new PreferenceStoreChangeEvent());
			}
		}.begin();
	}

	@Override
	public Instance begin() {
		this.add();
		return super.begin();
	}

	private void add() {
		PreferenceChangeListener.PREFERENCES.addPreferenceChangeListener(this);
	}

	@Override
	public Instance end() {
		this.remove();
		return super.end();
	}

	private void remove() {
		PreferenceChangeListener.PREFERENCES.removePreferenceChangeListener(this);
	}
}
