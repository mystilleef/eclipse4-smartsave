package com.laboki.eclipse.plugin.smartsave.instance;

public abstract class AbstractInstance implements Instance {

  protected AbstractInstance() {}

  @Override
  public Instance start() {
    return this;
  }

  @Override
  public Instance stop() {
    return this;
  }
}
