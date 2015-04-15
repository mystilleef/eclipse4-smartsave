package com.laboki.eclipse.plugin.smartsave.preferences;

import org.eclipse.core.runtime.jobs.Job;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class Updater extends AbstractEventBusInstance {

	private static final TaskMutexRule RULE = new TaskMutexRule();

	public Updater() {
		super();
	}

	@Subscribe
	public static
	void
	updatePreferences(final PreferenceStoreChangeEvent event) {
		new Task() {

			@Override
			public void execute() {
				Cache.INSTANCE.update();
			}
		}.setPriority(Job.INTERACTIVE).setRule(Updater.RULE).start();
	}
}
