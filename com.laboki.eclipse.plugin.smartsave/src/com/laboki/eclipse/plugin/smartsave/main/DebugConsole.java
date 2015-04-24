package com.laboki.eclipse.plugin.smartsave.main;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;

public enum DebugConsole {
	INSTANCE;

	private static final MessageConsole CONSOLE =
		DebugConsole.getConsole("Smart Save");
	private static final Logger LOGGER =
		Logger.getLogger(DebugConsole.class.getName());

	private static MessageConsole
	getConsole(final String name) {
		final MessageConsole console = DebugConsole.findConsole(name);
		if (console != null) DebugConsole.showPluginConsole();
		if (console != null) return console;
		return DebugConsole.newConsole(name);
	}

	private static MessageConsole
	findConsole(final String name) {
		final IConsole[] consoles =
			ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		for (final IConsole console : consoles)
			if (name.equals(console.getName())) return (MessageConsole) console;
		return null;
	}

	private static MessageConsole
	newConsole(final String name) {
		final MessageConsole myConsole = new MessageConsole(name, null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {
			myConsole
		});
		DebugConsole.showPluginConsole();
		return myConsole;
	}

	public static void
	out(final Object message) {
		DebugConsole.CONSOLE.newMessageStream().println(String.valueOf(message));
	}

	private static void
	showPluginConsole() {
		try {
			DebugConsole.tryToShowConsole();
		}
		catch (final PartInitException e) {
			DebugConsole.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private static void
	tryToShowConsole() throws PartInitException {
		((IConsoleView) EditorContext.WORKBENCH.getActiveWorkbenchWindow()
			.getActivePage()
			.showView(IConsoleConstants.ID_CONSOLE_VIEW)).display(DebugConsole.CONSOLE);
	}
}
