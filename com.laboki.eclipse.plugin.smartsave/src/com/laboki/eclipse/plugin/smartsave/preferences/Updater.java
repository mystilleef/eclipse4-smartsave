package com.laboki.eclipse.plugin.smartsave.preferences;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class Updater extends AbstractEventBusInstance {

	public Updater(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void updatePreferences() {
		new Task() {

			@Override
			public void execute() {
				Cache.INSTANCE.update();
			}
		}.begin();
	}
}
