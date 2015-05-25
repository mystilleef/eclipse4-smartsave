package com.laboki.eclipse.plugin.smartsave.main;

import com.laboki.eclipse.plugin.smartsave.commands.ToggleSmartSaveCommandState;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.listeners.AnnotationsListener;
import com.laboki.eclipse.plugin.smartsave.listeners.CompletionListener;
import com.laboki.eclipse.plugin.smartsave.listeners.DirtyPartListener;
import com.laboki.eclipse.plugin.smartsave.listeners.KeyEventListener;
import com.laboki.eclipse.plugin.smartsave.listeners.ListenerSwitch;
import com.laboki.eclipse.plugin.smartsave.listeners.PreferenceChangeListener;
import com.laboki.eclipse.plugin.smartsave.listeners.VerifyEventListener;
import com.laboki.eclipse.plugin.smartsave.main.services.BaseServices;

public final class PartServices extends BaseServices {

	@Override
	protected void
	startServices() {
		this.startService(new Saver());
		this.startService(new Scheduler());
		this.startService(new VerifyEventListener());
		this.startService(new AnnotationsListener());
		this.startService(new KeyEventListener());
		this.startService(new ListenerSwitch());
		this.startService(new CompletionListener());
		this.startService(new DirtyPartListener());
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
