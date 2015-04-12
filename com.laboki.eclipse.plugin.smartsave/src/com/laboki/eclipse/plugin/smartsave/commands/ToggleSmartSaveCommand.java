package com.laboki.eclipse.plugin.smartsave.commands;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.ui.commands.ICommandService;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.Activator;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.task.Task;


public final class ToggleSmartSaveCommand extends AbstractEventBusInstance {

	private static final String TOGGLE_STATE =
		"org.eclipse.ui.commands.toggleState";
	private static final String COMMAND_ID =
		"com.laboki.eclipse.plugin.smartsave.command.toggle.smart.save";
	protected static final Command COMMAND =
		ToggleSmartSaveCommand.getCommand();

	public ToggleSmartSaveCommand() {
		ToggleSmartSaveCommand.setToggleState(EditorContext.canSaveAutomatically());
	}

	private static Command getCommand() {
		return ((ICommandService) Activator.getInstance()
			.getWorkbench()
			.getService(ICommandService.class))
			.getCommand(ToggleSmartSaveCommand.COMMAND_ID);
	}

	@Subscribe
	public static void preferencesChanged(
		@SuppressWarnings("unused") final PreferenceStoreChangeEvent event) {
		new Task() {

			@Override
			public void execute() {
				ToggleSmartSaveCommand.setToggleState(EditorContext.canSaveAutomatically());
			}
		}.setDelay(EditorContext.SHORT_DELAY).start();
	}

	protected static void setToggleState(final boolean state) {
		ToggleSmartSaveCommand.getToggleState().setValue(state);
	}

	private static State getToggleState() {
		return ToggleSmartSaveCommand.COMMAND
			.getState(ToggleSmartSaveCommand.TOGGLE_STATE);
	}

}
