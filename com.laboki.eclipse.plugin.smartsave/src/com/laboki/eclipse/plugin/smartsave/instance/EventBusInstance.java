package com.laboki.eclipse.plugin.smartsave.instance;

import com.laboki.eclipse.plugin.smartsave.main.EventBus;

public abstract class EventBusInstance extends BaseInstance {

	private boolean isRegistered = false;

	@Override
	public Instance
	start() {
		if (this.isRegistered) return this;
		EventBus.register(this);
		this.isRegistered = true;
		return this;
	}

	@Override
	public Instance
	stop() {
		if (!this.isRegistered) return this;
		EventBus.unregister(this);
		this.isRegistered = false;
		return this;
	}
}
