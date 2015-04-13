package com.laboki.eclipse.plugin.smartsave.commands;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.ui.commands.ICommandService;

import com.laboki.eclipse.plugin.smartsave.Activator;


public enum ToggleSmartSaveCommand {

	INSTANCE;

	private static final String STATE_ID =
		"org.eclipse.ui.commands.toggleState";
	public static final String ID =
		"com.laboki.eclipse.plugin.smartsave.command.toggleSmartSave";
	public static final Command COMMAND =
		ToggleSmartSaveCommand.getCommand();
	public static final State STATE =
		ToggleSmartSaveCommand.getStateInstance();

	private static Command getCommand() {
		return ((ICommandService) Activator.getInstance()
			.getWorkbench()
			.getService(ICommandService.class))
			.getCommand(ToggleSmartSaveCommand.ID);
	}

	private static State getStateInstance() {
		return ToggleSmartSaveCommand.COMMAND
			.getState(ToggleSmartSaveCommand.STATE_ID);
	}

	public static boolean getState() {
		return (boolean) ToggleSmartSaveCommand.STATE.getValue();
	}

	public static void setState(final boolean state) {
		ToggleSmartSaveCommand.STATE.setValue(state);
	}

}
