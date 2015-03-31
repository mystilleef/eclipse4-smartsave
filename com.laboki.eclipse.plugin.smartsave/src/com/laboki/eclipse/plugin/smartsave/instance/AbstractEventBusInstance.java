package com.laboki.eclipse.plugin.smartsave.instance;

import com.laboki.eclipse.plugin.smartsave.main.EventBus;

public abstract class AbstractEventBusInstance implements Instance {

  private boolean isRegistered = false;

  protected AbstractEventBusInstance() {}

  @Override
  public Instance begin() {
    if (this.isRegistered) return this;
    EventBus.register(this);
    this.isRegistered = true;
    return this;
  }

  @Override
  public Instance end() {
    if (!this.isRegistered) return this;
    EventBus.unregister(this);
    this.isRegistered = false;
    return this;
  }
}
