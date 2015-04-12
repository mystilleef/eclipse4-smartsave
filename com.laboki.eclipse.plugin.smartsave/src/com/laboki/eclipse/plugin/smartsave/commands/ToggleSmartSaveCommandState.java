package com.laboki.eclipse.plugin.smartsave.commands;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.task.Task;


public final class ToggleSmartSaveCommandState extends AbstractEventBusInstance {


	public ToggleSmartSaveCommandState() {
		ToggleSmartSaveCommandState.updateState();
	}

	@Subscribe
	public static void preferencesChanged(
		@SuppressWarnings("unused") final PreferenceStoreChangeEvent event) {
		new Task() {
	
			@Override
			public void execute() {
				ToggleSmartSaveCommandState.updateState();
			}
		}.setDelay(EditorContext.SHORT_DELAY).start();
	}

	protected static void updateState() {
		ToggleSmartSaveCommand.setState(EditorContext.canSaveAutomatically());
	}
}
