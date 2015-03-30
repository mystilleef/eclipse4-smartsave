
package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;

public final class SaveJob extends WorkspaceJob implements Runnable {

  private static final String JOB_FAMILY = "++SAVE_WORKSPACE_JOB_FAMILY++";
  private IEditorPart editor;

  public SaveJob() {
    super("SMART_SAVE_WORKSPACE_SAVE_JOB");
    this.setProperties();
  }

  private void setProperties() {
    this.setPriority(Job.DECORATE);
    this.setUser(false);
    this.setSystem(true);
    this.setRule(this.new MutexRule());
  }

  @Override
  public void run() {
    this.save();
  }

  private void save() {
    EditorContext.flushEvents();
    this.editor.getSite().getPage().saveEditor(this.editor, false);
    EditorContext.flushEvents();
  }

  @Override
  public IStatus runInWorkspace(final IProgressMonitor monitor) {
    if (monitor.isCanceled()) return Status.CANCEL_STATUS;
    EditorContext.asyncExec(this);
    return Status.OK_STATUS;
  }

  @Override
  public boolean belongsTo(final Object family) {
    return family.equals(SaveJob.JOB_FAMILY);
  }

  @Override
  public boolean shouldSchedule() {
    return super.shouldSchedule() && SaveJob.jobDoesNotExists();
  }

  private static boolean jobDoesNotExists() {
    return Job.getJobManager().find(SaveJob.JOB_FAMILY).length == 0;
  }

  public void execute(final IEditorPart editorPart) {
    this.editor = editorPart;
    this.setRule(EditorContext.getFile(editorPart));
    this.schedule(100);
  }

  private class MutexRule implements ISchedulingRule {

    public MutexRule() {}

    @Override
    public boolean isConflicting(final ISchedulingRule rule) {
      return rule == this;
    }

    @Override
    public boolean contains(final ISchedulingRule rule) {
      return rule == this;
    }
  }
}
