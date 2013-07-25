package com.laboki.eclipse.plugin.smartsave.main;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.listeners.AnnotationsListener;
import com.laboki.eclipse.plugin.smartsave.listeners.CompletionListener;
import com.laboki.eclipse.plugin.smartsave.listeners.DirtyPartListener;
import com.laboki.eclipse.plugin.smartsave.listeners.KeyEventListener;
import com.laboki.eclipse.plugin.smartsave.listeners.ListenerSwitch;
import com.laboki.eclipse.plugin.smartsave.listeners.PreferenceChangeListener;
import com.laboki.eclipse.plugin.smartsave.listeners.VerifyEventListener;
import com.laboki.eclipse.plugin.smartsave.preferences.Updater;

public final class Services implements Instance {

	private final List<Instance> instances = Lists.newArrayList();

	@Override
	public Instance begin() {
		this.startServices();
		return this;
	}

	private void startServices() {
		this.startService(new Saver(EditorContext.EVENT_BUS));
		this.startService(new FileSyncer(EditorContext.EVENT_BUS));
		this.startService(new Scheduler(EditorContext.EVENT_BUS));
		this.startService(new VerifyEventListener(EditorContext.EVENT_BUS));
		this.startService(new AnnotationsListener(EditorContext.EVENT_BUS));
		this.startService(new KeyEventListener(EditorContext.EVENT_BUS));
		this.startService(new ListenerSwitch(EditorContext.EVENT_BUS));
		this.startService(new CompletionListener(EditorContext.EVENT_BUS));
		this.startService(new DirtyPartListener(EditorContext.EVENT_BUS));
		this.startService(new Updater(EditorContext.EVENT_BUS));
		this.startService(new PreferenceChangeListener(EditorContext.EVENT_BUS));
	}

	private void startService(final Instance instance) {
		instance.begin();
		this.instances.add(instance);
	}

	@Override
	public Instance end() {
		this.stopServices();
		this.instances.clear();
		return this;
	}

	private void stopServices() {
		for (final Instance instance : ImmutableList.copyOf(this.instances))
			this.stopService(instance);
	}

	private void stopService(final Instance instance) {
		this.instances.remove(instance);
		instance.end();
	}
}
