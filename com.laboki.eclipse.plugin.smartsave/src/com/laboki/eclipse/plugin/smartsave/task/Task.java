package com.laboki.eclipse.plugin.smartsave.task;

import org.eclipse.core.runtime.jobs.Job;

public class Task extends AbstractTask {

  public Task() {
    super("", 0, Job.INTERACTIVE);
  }

  public Task(final String name) {
    super(name, 0, Job.INTERACTIVE);
  }

  public Task(final int delayTime) {
    super("", delayTime, Job.DECORATE);
  }

  public Task(final String name, final int delayTime) {
    super(name, delayTime, Job.DECORATE);
  }

  public Task(final String name, final int delayTime, final int priority) {
    super(name, delayTime, priority);
  }

  @Override
  protected void runTask() {
    this.runExecute();
  }
}
