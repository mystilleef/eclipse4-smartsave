package com.laboki.eclipse.plugin.smartsave.saver;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.listeners.DirtyPartListener;
import com.laboki.eclipse.plugin.smartsave.listeners.SaverAnnotationsListener;
import com.laboki.eclipse.plugin.smartsave.listeners.SaverCompletionListener;
import com.laboki.eclipse.plugin.smartsave.listeners.SaverKeyEventListener;
import com.laboki.eclipse.plugin.smartsave.listeners.SaverVerifyListener;

public final class Services implements Instance {

	private final List<Instance> instances = Lists.newArrayList();
	private final EventBus eventBus = new EventBus();

	@Override
	public Instance begin() {
		this.startServices();
		return this;
	}

	private void startServices() {
		this.startService(new Saver(this.eventBus));
		this.startService(new FileSyncer(this.eventBus));
		this.startService(new Scheduler(this.eventBus));
		this.startService(new SaverVerifyListener(this.eventBus));
		this.startService(new SaverAnnotationsListener(this.eventBus));
		this.startService(new SaverKeyEventListener(this.eventBus));
		this.startService(new ListenerToggler(this.eventBus));
		this.startService(new SaverCompletionListener(this.eventBus));
		this.startService(new DirtyPartListener(this.eventBus));
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
