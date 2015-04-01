package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;

public final class SaveJob extends WorkspaceJob implements Runnable {

  public static final String JOB_FAMILY = "++SAVE_WORKSPACE_JOB_FAMILY++";
  private IEditorPart editor;

  public SaveJob() {
    super("SMART_SAVE_WORKSPACE_SAVE_JOB");
    this.setProperties();
  }

  private void setProperties() {
    this.setPriority(Job.DECORATE);
    this.setUser(false);
    this.setSystem(true);
  }

  @Override
  public void run() {
    this.save();
  }

  private void save() {
    this.editor.getSite().getPage().saveEditor(this.editor, false);
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
    return EditorContext.canScheduleSave();
  }

  public static boolean doesNotExists() {
    return Job.getJobManager().find(SaveJob.JOB_FAMILY).length == 0;
  }

  public void execute(final IEditorPart editorPart) {
    this.setNewRule(editorPart);
    this.editor = editorPart;
    this.schedule(EditorContext.SHORT_DELAY);
  }

  private void setNewRule(final IEditorPart editorPart) {
    if (this.editor == editorPart) return;
    this.setRule(EditorContext.getFile(editorPart));
  }
}
