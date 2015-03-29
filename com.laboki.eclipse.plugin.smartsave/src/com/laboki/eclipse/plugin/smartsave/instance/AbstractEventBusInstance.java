
package com.laboki.eclipse.plugin.smartsave.instance;

import com.laboki.eclipse.plugin.smartsave.main.EventBus;

public abstract class AbstractEventBusInstance implements Instance {

  protected final EventBus eventBus;
  boolean isRegistered = false;

  protected AbstractEventBusInstance(final EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public Instance begin() {
    if (this.isRegistered) return this;
    this.eventBus.register(this);
    this.isRegistered = true;
    return this;
  }

  @Override
  public Instance end() {
    if (!this.isRegistered) return this;
    this.eventBus.unregister(this);
    this.isRegistered = false;
    return this;
  }
}
