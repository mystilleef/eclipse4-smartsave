
package com.laboki.eclipse.plugin.smartsave;

import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.Factory;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public enum Plugin implements Instance {
  INSTANCE;

  @Override
  public Instance begin() {
    new AsyncTask() {

      @Override
      public void asyncExecute() {
        Factory.INSTANCE.begin();
      }
    }.begin();
    return this;
  }

  @Override
  public Instance end() {
    new AsyncTask() {

      @Override
      public void asyncExecute() {
        Factory.INSTANCE.end();
      }
    }.begin();
    return this;
  }
}
