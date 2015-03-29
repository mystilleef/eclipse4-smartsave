
package com.laboki.eclipse.plugin.smartsave.instance;

public abstract class AbstractInstance implements Instance {

  protected AbstractInstance() {}

  @Override
  public Instance begin() {
    return this;
  }

  @Override
  public Instance end() {
    return this;
  }
}
