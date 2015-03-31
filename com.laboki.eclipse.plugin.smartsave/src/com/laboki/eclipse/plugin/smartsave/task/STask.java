
package com.laboki.eclipse.plugin.smartsave.task;

import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public abstract class STask extends BaseTask implements ExecuteTask {

  public STask() {}

  @Override
  protected TaskJob newTaskJob() {
    return new TaskJob() {

      @Override
      protected void runTask() {
        EditorContext.syncExec(() -> STask.this.execute());
      }
    };
  }

  @Override
  public abstract void execute();
}
