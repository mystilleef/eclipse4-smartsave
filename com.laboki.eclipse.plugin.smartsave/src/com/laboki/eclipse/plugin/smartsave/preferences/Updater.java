package com.laboki.eclipse.plugin.smartsave.preferences;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class Updater extends AbstractEventBusInstance {

	public Updater(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public static void updatePreferences(@SuppressWarnings("unused") final PreferenceStoreChangeEvent event) {
		new Task() {

			@Override
			public void execute() {
				Cache.INSTANCE.update();
			}
		}.begin();
	}
}
