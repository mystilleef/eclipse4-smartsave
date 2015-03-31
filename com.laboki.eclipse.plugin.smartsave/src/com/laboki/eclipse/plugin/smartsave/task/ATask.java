package com.laboki.eclipse.plugin.smartsave.task;

import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public abstract class ATask extends BaseTask implements ExecuteTask {

  public ATask() {}

  @Override
  protected TaskJob newTaskJob() {
    return new TaskJob() {

      @Override
      protected void runTask() {
        EditorContext.asyncExec(() -> ATask.this.execute());
      }
    };
  }

  @Override
  public abstract void execute();
}
