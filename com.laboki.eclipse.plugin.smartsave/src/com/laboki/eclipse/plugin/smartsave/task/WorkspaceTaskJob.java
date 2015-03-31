package com.laboki.eclipse.plugin.smartsave.task;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public abstract class WorkspaceTaskJob extends WorkspaceJob {

  private Object family;

  public WorkspaceTaskJob() {
    super("");
  }

  @Override
  public boolean belongsTo(final Object family) {
    if (this.family == null) return false;
    if (family == null) return false;
    return this.family.equals(family);
  }

  @Override
  public IStatus runInWorkspace(final IProgressMonitor monitor)
    throws CoreException {
    if (monitor.isCanceled()) return Status.CANCEL_STATUS;
    this.runTask();
    return Status.OK_STATUS;
  }

  protected void runTask() {}

  public void setFamily(final Object family) {
    this.family = family;
  }
}
