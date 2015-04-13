package com.laboki.eclipse.plugin.smartsave.preferences;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class Updater extends AbstractEventBusInstance {

	private static final String TASK_NAME = "update preferences task";
	private static final TaskMutexRule RULE = new TaskMutexRule();

	public Updater() {
		super();
	}

	@Subscribe
	@AllowConcurrentEvents
	public static
	void updatePreferences(final PreferenceStoreChangeEvent event) {
		new Task() {

			@Override
			public void execute() {
				Cache.INSTANCE.update();
			}
		}.setName(Updater.TASK_NAME).setRule(Updater.RULE).start();
	}
}
