package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class PreferenceChangeListener extends EventBusInstance
	implements
		IPreferenceChangeListener {

	private static final int DELAY = 125;
	private static final String FAMILY = "Preferences Change Listener Family";
	private static final TaskMutexRule RULE = new TaskMutexRule();
	private static final String TASK_NAME =
		"smartsave preference change event listener";
	private static final IEclipsePreferences PREFERENCES =
		Store.getPreferences();

	@Override
	public void
	preferenceChange(final PreferenceChangeEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				EventBus.post(new PreferenceStoreChangeEvent());
			}
		}.setFamily(PreferenceChangeListener.FAMILY)
			.setName(PreferenceChangeListener.TASK_NAME)
			.setRule(PreferenceChangeListener.RULE)
			.setDelay(PreferenceChangeListener.DELAY)
			.start();
	}

	@Override
	public Instance
	start() {
		PreferenceChangeListener.PREFERENCES.addPreferenceChangeListener(this);
		return super.start();
	}

	@Override
	public Instance
	stop() {
		PreferenceChangeListener.PREFERENCES.removePreferenceChangeListener(this);
		return super.stop();
	}
}
