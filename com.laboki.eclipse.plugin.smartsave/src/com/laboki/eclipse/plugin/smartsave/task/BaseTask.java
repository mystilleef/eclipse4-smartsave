
package com.laboki.eclipse.plugin.smartsave.task;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.smartsave.instance.Instance;

public abstract class BaseTask implements Runnable, Instance {

  private final TaskJob job;
  private long delay = 0;

  public BaseTask() {
    this.job = this.newTaskJob();
    this.setDefaultProperties();
  }

  protected abstract TaskJob newTaskJob();

  private void setDefaultProperties() {
    this.job.setUser(false);
    this.job.setSystem(true);
  }

  @Override
  public void run() {
    this.begin();
  }

  @Override
  public Instance begin() {
    if (!this.shouldSchedule()) return this;
    this.job.schedule(this.delay);
    return this;
  }

  @Override
  public Instance end() {
    this.job.cancel();
    return this;
  }

  protected BaseTask setDelay(final long delay) {
    this.delay = delay;
    return this;
  }

  protected BaseTask setFamily(final Object family) {
    this.job.setFamily(family);
    return this;
  }

  protected BaseTask setName(final String name) {
    this.job.setName(name);
    return this;
  }

  protected BaseTask setPriority(final int priority) {
    this.job.setPriority(priority);
    return this;
  }

  protected BaseTask setUser(final boolean isUserTask) {
    this.job.setUser(isUserTask);
    return this;
  }

  protected BaseTask setSystem(final boolean isSystemTask) {
    this.job.setSystem(isSystemTask);
    return this;
  }

  protected BaseTask setRule(final ISchedulingRule rule) {
    this.job.setRule(rule);
    return this;
  }

  protected boolean belongsTo(final Object family) {
    return this.job.belongsTo(family);
  }

  protected boolean shouldSchedule() {
    return this.job.shouldSchedule();
  }

  protected static boolean taskFamilyExists(final Object family) {
    return Job.getJobManager().find(family).length == 0;
  }
}
