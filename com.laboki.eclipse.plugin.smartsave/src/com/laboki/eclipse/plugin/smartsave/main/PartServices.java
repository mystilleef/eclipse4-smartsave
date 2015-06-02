package com.laboki.eclipse.plugin.smartsave.main;

import com.laboki.eclipse.plugin.smartsave.checkers.BlacklistChecker;
import com.laboki.eclipse.plugin.smartsave.checkers.BusyChecker;
import com.laboki.eclipse.plugin.smartsave.checkers.DirtyChecker;
import com.laboki.eclipse.plugin.smartsave.checkers.ErrorChecker;
import com.laboki.eclipse.plugin.smartsave.checkers.PreferencesChecker;
import com.laboki.eclipse.plugin.smartsave.checkers.WarningChecker;
import com.laboki.eclipse.plugin.smartsave.commands.ToggleSmartSaveCommandState;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.listeners.AnnotationsListener;
import com.laboki.eclipse.plugin.smartsave.listeners.CompletionListener;
import com.laboki.eclipse.plugin.smartsave.listeners.DocumentListener;
import com.laboki.eclipse.plugin.smartsave.listeners.KeyEventListener;
import com.laboki.eclipse.plugin.smartsave.listeners.PreferenceChangeListener;
import com.laboki.eclipse.plugin.smartsave.main.services.BaseServices;

public final class PartServices extends BaseServices {

	@Override
	protected void
	startServices() {
		this.startService(new FinalSaver());
		this.startService(new Saver());
		this.startService(new WarningChecker());
		this.startService(new ErrorChecker());
		this.startService(new BusyChecker());
		this.startService(new DirtyChecker());
		this.startService(new BlacklistChecker());
		this.startService(new PreferencesChecker());
		this.startService(new Scheduler());
		this.startService(new DocumentListener());
		this.startService(new AnnotationsListener());
		this.startService(new KeyEventListener());
		this.startService(new CompletionListener());
		this.startService(new ToggleSmartSaveCommandState());
		this.startService(new PreferenceChangeListener());
	}

	@Override
	protected void
	cancelTasks() {
		EditorContext.cancelSaverTasks();
		super.cancelTasks();
	}
}
