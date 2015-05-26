package com.laboki.eclipse.plugin.smartsave.contexts;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.CheckBlacklistEvent;
import com.laboki.eclipse.plugin.smartsave.events.CheckPreferencesEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.main.Scheduler;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class PreferencesChecker extends EventBusInstance {

	@Subscribe
	@AllowConcurrentEvents
	public static void
	eventHandler(final CheckPreferencesEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				if (Store.getCanSaveAutomatically()) this.broadcastEvent();
			}

			private void
			broadcastEvent() {
				EventBus.post(new CheckBlacklistEvent());
			}
		}.setDelay(Scheduler.DELAY)
			.setFamily(Scheduler.FAMILY)
			.setRule(Scheduler.RULE)
			.start();
	}
}
