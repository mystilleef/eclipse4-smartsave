package com.laboki.eclipse.plugin.smartsave.task;

public abstract class RTask extends BaseTask implements ExecuteTask {

  public RTask() {}

  @Override
  protected TaskJob newTaskJob() {
    return new TaskJob() {

      @Override
      protected void runTask() {
        RTask.this.execute();
      }
    };
  }

  @Override
  public abstract void execute();
}
