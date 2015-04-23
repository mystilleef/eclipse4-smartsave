package com.laboki.eclipse.plugin.smartsave.commands;

import org.eclipse.core.runtime.jobs.Job;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class ToggleSmartSaveCommandState
	extends
		AbstractEventBusInstance {

	private static final int ONE_SECOND = 1000;
	private static final String FAMILY = "Toggle Smart Save Command Family";
	private static final TaskMutexRule RULE = new TaskMutexRule();

	public ToggleSmartSaveCommandState() {
		ToggleSmartSaveCommandState.updateState();
	}

	@Subscribe
	public static void
	preferencesChanged(final PreferenceStoreChangeEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				ToggleSmartSaveCommandState.updateState();
			}
		}.setFamily(ToggleSmartSaveCommandState.FAMILY)
			.setPriority(Job.INTERACTIVE)
			.setRule(ToggleSmartSaveCommandState.RULE)
			.setDelay(ToggleSmartSaveCommandState.ONE_SECOND)
			.start();
	}

	protected static void
	updateState() {
		ToggleSmartSaveCommand.setState(EditorContext.canSaveAutomatically());
	}
}
