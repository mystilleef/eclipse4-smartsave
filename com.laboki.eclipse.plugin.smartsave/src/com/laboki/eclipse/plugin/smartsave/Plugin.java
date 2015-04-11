package com.laboki.eclipse.plugin.smartsave;

import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.Factory;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public enum Plugin implements Instance {
  INSTANCE;

  @Override
  public Instance start() {
    new AsyncTask() {

      @Override
      public void execute() {
        Factory.INSTANCE.start();
      }
    }.start();
    return this;
  }

  @Override
  public Instance stop() {
    Factory.INSTANCE.stop();
    return this;
  }
}
