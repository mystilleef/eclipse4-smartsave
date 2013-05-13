package com.laboki.eclipse.plugin.smartsave.instance;

import com.laboki.eclipse.plugin.smartsave.saver.EventBus;

public abstract class AbstractEventBusInstance implements Instance {

	protected final EventBus eventBus;

	protected AbstractEventBusInstance(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		return this;
	}
}
