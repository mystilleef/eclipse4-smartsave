package com.laboki.eclipse.plugin.smartsave.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public abstract class AbstractTask extends Job implements Runnable, Instance {

  private int delayTime;
  private final String name;

  protected AbstractTask(final String name, final int delayTime,
    final int priority) {
    super(name);
    this.name = name;
    this.delayTime = delayTime;
    this.setPriority(priority);
  }

  @Override
  public boolean belongsTo(final Object family) {
    return this.name.equals(family);
  }

  @Override
  public Instance end() {
    this.cancel();
    return this;
  }

  protected AbstractTask setTaskName(final String name) {
    this.setName(name);
    return this;
  }

  protected AbstractTask setTastPriority(final int priority) {
    this.setPriority(priority);
    return this;
  }

  protected AbstractTask setTaskUser(final boolean user) {
    this.setUser(user);
    return this;
  }

  protected AbstractTask setTaskSystem(final boolean system) {
    this.setSystem(system);
    return this;
  }

  public AbstractTask setTaskRule(final ISchedulingRule rule) {
    this.setRule(rule);
    return this;
  }

  protected AbstractTask setTaskDelay(final int delay) {
    this.delayTime = delay;
    return this;
  }

  @Override
  public Instance begin() {
    this.setUser(false);
    this.setSystem(true);
    this.schedule(this.delayTime);
    return this;
  }

  @Override
  public void run() {
    this.begin();
  }

  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    if (monitor.isCanceled()) return Status.CANCEL_STATUS;
    this.runTask();
    return Status.OK_STATUS;
  }

  protected void runTask() {}

  protected void runExecute() {
    this.execute();
  }

  protected void execute() {}

  protected void runAsyncExecute() {
    EditorContext.asyncExec(() -> {
      AbstractTask.this.asyncExecute();
    });
  }

  protected void asyncExecute() {}

  protected void runSyncExecute() {
    EditorContext.syncExec(() -> {
      AbstractTask.this.syncExecute();
    });
  }

  protected void syncExecute() {}
}
