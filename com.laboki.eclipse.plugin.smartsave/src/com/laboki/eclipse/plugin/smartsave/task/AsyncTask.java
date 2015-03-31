package com.laboki.eclipse.plugin.smartsave.task;

import org.eclipse.core.runtime.jobs.Job;

public class AsyncTask extends AbstractTask {

  public AsyncTask() {
    super("", 0, Job.INTERACTIVE);
  }

  public AsyncTask(final String name) {
    super(name, 0, Job.INTERACTIVE);
  }

  public AsyncTask(final int delayTime) {
    super("", delayTime, Job.DECORATE);
  }

  public AsyncTask(final String name, final int delayTime) {
    super(name, delayTime, Job.DECORATE);
  }

  public AsyncTask(final String name, final int delayTime, final int priority) {
    super(name, delayTime, priority);
  }

  @Override
  protected void runTask() {
    this.runAsyncExecute();
  }
}
