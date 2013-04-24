package com.laboki.eclipse.plugin.smartsave.saver;

import java.util.List;
import java.util.logging.Level;

import lombok.extern.java.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.DirtyPartMonitor;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverAnnotationsListener;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverKeyEventListener;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverVerifyListener;

@Log
public final class SaverServices implements Instance {

	private final List<Instance> instances = Lists.newArrayList();
	private final EventBus eventBus = new EventBus();

	@Override
	public Instance begin() {
		this.startServices();
		return this;
	}

	private void startServices() {
		this.tryToStartService(new Saver(this.eventBus));
		this.tryToStartService(new FileSyncer(this.eventBus));
		this.tryToStartService(new BusyDetector(this.eventBus));
		this.tryToStartService(new SaveScheduler(this.eventBus));
		this.tryToStartService(new SaverVerifyListener(this.eventBus));
		this.tryToStartService(new SaverAnnotationsListener(this.eventBus));
		this.tryToStartService(new SaverKeyEventListener(this.eventBus));
		this.tryToStartService(new DirtyPartMonitor(this.eventBus));
	}

	private void tryToStartService(final Instance instance) {
		try {
			this.startService(instance);
		} catch (final Exception e) {
			SaverServices.log.log(Level.FINEST, "failed to start service");
		}
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
		instance.end();
		this.instances.remove(instance);
	}
}
