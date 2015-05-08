package com.laboki.eclipse.plugin.smartsave.main;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.smartsave.commands.ToggleSmartSaveCommandState;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.listeners.AnnotationsListener;
import com.laboki.eclipse.plugin.smartsave.listeners.CompletionListener;
import com.laboki.eclipse.plugin.smartsave.listeners.DirtyPartListener;
import com.laboki.eclipse.plugin.smartsave.listeners.KeyEventListener;
import com.laboki.eclipse.plugin.smartsave.listeners.ListenerSwitch;
import com.laboki.eclipse.plugin.smartsave.listeners.PreferenceChangeListener;
import com.laboki.eclipse.plugin.smartsave.listeners.VerifyEventListener;

public final class Services implements Instance {

	private final List<Instance> instances = Lists.newArrayList();

	@Override
	public Instance
	start() {
		this.startServices();
		return this;
	}

	private void
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

	private void
	startService(final Instance instance) {
		instance.start();
		this.instances.add(instance);
	}

	@Override
	public Instance
	stop() {
		Services.cancelTasks();
		this.stopServices();
		this.instances.clear();
		return this;
	}

	private static void
	cancelTasks() {
		EditorContext.cancelAllSaverTasks();
		EditorContext.cancelEventTasks();
	}

	private void
	stopServices() {
		for (final Instance instance : ImmutableList.copyOf(this.instances))
			this.stopService(instance);
	}

	private void
	stopService(final Instance instance) {
		instance.stop();
		this.instances.remove(instance);
	}
}
