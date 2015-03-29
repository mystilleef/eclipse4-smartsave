
package com.laboki.eclipse.plugin.smartsave.main;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.smartsave.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.preferences.Cache;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public enum EditorContext {
  INSTANCE;

  public static final String SCHEDULER_ENABLE_SAVE_LISTENERS_TASK =
      "smartsave scheduler enable save listeners event task";
  public static final String FILE_SYNCER_TASK = "file syncer task";
  public static final String LISTENER_TASK = "Listener Task";
  public static final String PLUGIN_NAME =
      "com.laboki.eclipse.plugin.smartsave";
  public static final String CONTRIBUTOR_URI = MessageFormat.format(
    "plugin://{0}", EditorContext.PLUGIN_NAME);
  public static final String CONTRIBUTION_URI = "bundleclass://{0}/{1}";
  public static final int SHORT_DELAY_TIME = 250;
  public static final String AUTOMATIC_SAVER_TASK =
      "smartsave automatic saver task";
  public static final String LISTENER_SAVER_TASK =
      "smartsave listener saver task";
  public static final String SCHEDULED_SAVER_TASK =
      "smartsave scheduled saver task";
  public static final IJobManager JOB_MANAGER = Job.getJobManager();
  public static final IWorkbench WORKBENCH = PlatformUI.getWorkbench();
  public static final Display DISPLAY = EditorContext.WORKBENCH.getDisplay();
  private static final int MILLI_SECONDS_UNIT = 1000;
  private static final int SAVE_INTERVAL_DIFFERENCIAL = 750;
  private static final String LINK_SLAVE =
      "org.eclipse.ui.internal.workbench.texteditor.link.slave";
  private static final String LINK_MASTER =
      "org.eclipse.ui.internal.workbench.texteditor.link.master";
  private static final String LINK_TARGET =
      "org.eclipse.ui.internal.workbench.texteditor.link.target";
  private static final String LINK_EXIT =
      "org.eclipse.ui.internal.workbench.texteditor.link.exit";
  private static final String ANNOTATION_WARNING =
      "org.eclipse.ui.workbench.texteditor.warning";
  private static final String ANNOTATION_ERROR =
      "org.eclipse.ui.workbench.texteditor.error";
  private static final List<String> LINK_ANNOTATIONS = Lists.newArrayList(
    EditorContext.LINK_EXIT, EditorContext.LINK_TARGET,
    EditorContext.LINK_MASTER, EditorContext.LINK_SLAVE);
  private static final Cache PREFERENCE = Cache.INSTANCE;
  private static final Logger LOGGER = Logger.getLogger(EditorContext.class
    .getName());
  private final static DefaultMarkerAnnotationAccess ANNOTATION_ACCESS =
      new DefaultMarkerAnnotationAccess();

  public static void asyncExec(final Runnable runnable) {
    if (EditorContext.displayIsDisposed()) return;
    EditorContext.DISPLAY.asyncExec(runnable);
  }

  public static void syncExec(final Runnable runnable) {
    if (EditorContext.displayIsDisposed()) return;
    EditorContext.DISPLAY.syncExec(runnable);
  }

  private static boolean displayIsDisposed() {
    return (EditorContext.DISPLAY == null)
        || EditorContext.DISPLAY.isDisposed();
  }

  public static IPartService getPartService() {
    return (IPartService) EditorContext.WORKBENCH.getActiveWorkbenchWindow()
        .getService(IPartService.class);
  }

  public static Shell getShell() {
    return EditorContext.WORKBENCH.getModalDialogShellProvider().getShell();
  }

  public static IEditorPart getEditor() {
    return EditorContext.WORKBENCH.getActiveWorkbenchWindow().getActivePage()
        .getActiveEditor();
  }

  public static Control getControl(final IEditorPart editor) {
    return (Control) editor.getAdapter(Control.class);
  }

  public static StyledText getBuffer(final IEditorPart editor) {
    return (StyledText) EditorContext.getControl(editor);
  }

  public static SourceViewer getView(final IEditorPart editor) {
    return (SourceViewer) editor.getAdapter(ITextOperationTarget.class);
  }

  public static void tryToSave(final IEditorPart editor) {
    try {
      if (EditorContext.canSave(editor)) EditorContext.save(editor);
    }
    catch (final Exception e) {
      EditorContext.save(editor);
    }
  }

  private static boolean canSave(final IEditorPart editor) {
    return EditorContext.canSaveAutomatically()
        && EditorContext.canSaveFile(editor);
  }

  public static boolean canSaveAutomatically() {
    return EditorContext.PREFERENCE.canSaveAutomatically();
  }

  private static boolean canSaveFile(final IEditorPart editor) {
    return !(EditorContext.isNotModified(editor)
        || EditorContext.isBeingEdited(editor) || EditorContext
        .hasProblems(editor));
  }

  private static boolean isBeingEdited(final IEditorPart editor) {
    return EditorContext.hasSelection(editor)
        || EditorContext.isInLinkMode(editor);
  }

  private static boolean hasProblems(final IEditorPart editor) {
    return EditorContext.bufferHasErrors(editor)
        || EditorContext.bufferHasWarnings(editor);
  }

  public static boolean isNotModified(final IEditorPart editor) {
    return !EditorContext.isModified(editor);
  }

  public static boolean isModified(final IEditorPart editor) {
    return editor.isDirty();
  }

  public static boolean hasSelection(final IEditorPart editor) {
    return (EditorContext.getBuffer(editor).getSelectionCount() > 0)
        || EditorContext.getBuffer(editor).getBlockSelection();
  }

  public static boolean isInLinkMode(final IEditorPart editor) {
    return EditorContext.hasLinkAnnotations(editor);
  }

  private static boolean hasLinkAnnotations(final IEditorPart editor) {
    final Iterator<Annotation> iterator =
        EditorContext.getView(editor).getAnnotationModel()
        .getAnnotationIterator();
    while (iterator.hasNext())
      if (EditorContext.isLinkModeAnnotation(iterator)) return true;
    return false;
  }

  private static boolean isLinkModeAnnotation(
    final Iterator<Annotation> iterator) {
    if (EditorContext.LINK_ANNOTATIONS.contains(iterator.next().getType())) return true;
    return false;
  }

  private static boolean bufferHasErrors(final IEditorPart editor) {
    if (EditorContext.canSaveIfErrors()) return false;
    return EditorContext.hasErrors(editor);
  }

  public static boolean canSaveIfErrors() {
    return EditorContext.PREFERENCE.canSaveIfErrors();
  }

  public static boolean hasErrors(final IEditorPart editor) {
    return EditorContext.getAnnotationSeverity(EditorContext.ANNOTATION_ERROR,
      editor);
  }

  private static boolean bufferHasWarnings(final IEditorPart editor) {
    if (EditorContext.canSaveIfWarnings()) return false;
    return EditorContext.hasWarnings(editor);
  }

  public static boolean canSaveIfWarnings() {
    return EditorContext.PREFERENCE.canSaveIfWarnings();
  }

  public static boolean hasWarnings(final IEditorPart editor) {
    return EditorContext.getAnnotationSeverity(
      EditorContext.ANNOTATION_WARNING, editor);
  }

  private static boolean getAnnotationSeverity(final String problemSeverity,
    final IEditorPart editor) {
    final Iterator<Annotation> iterator =
        EditorContext.getView(editor).getAnnotationModel()
        .getAnnotationIterator();
    while (iterator.hasNext())
      if (EditorContext.hasProblems(problemSeverity, iterator)) return true;
    return false;
  }

  private static boolean hasProblems(final String problemSeverity,
    final Iterator<Annotation> iterator) {
    final Annotation annotation = iterator.next();
    if (annotation.isMarkedDeleted()) return false;
    return EditorContext.ANNOTATION_ACCESS.isSubtype(annotation.getType(),
      problemSeverity);
  }

  public static void forceSave() {
    EditorContext.WORKBENCH.saveAllEditors(false);
  }

  public static void save(final IEditorPart editor) {
    INSTANCE.new SaveWorkspaceJob(editor).execute();
  }

  class SaveWorkspaceJob extends WorkspaceJob implements Runnable {

    private final IEditorPart editor;
    public static final String SAVE_WORKSPACE_JOB_FAMILY =
        "saveworkspacejobfamily";

    public SaveWorkspaceJob(final IEditorPart editor) {
      super("Save Workspace Job");
      this.editor = editor;
    }

    @Override
    public void run() {
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
      return family.equals(SaveWorkspaceJob.SAVE_WORKSPACE_JOB_FAMILY);
    }

    @Override
    public boolean shouldSchedule() {
      return super.shouldSchedule() && this.jobDoesNotExists()
        && Job.getJobManager().isIdle();
    }

    private boolean jobDoesNotExists() {
      return Job.getJobManager().find(
        SaveWorkspaceJob.SAVE_WORKSPACE_JOB_FAMILY).length == 0;
    }

    @Override
    public boolean shouldRun() {
      return true;
    }

    public void execute() {
      this.setPriority(Job.DECORATE);
      this.setUser(false);
      this.setSystem(true);
      this.setRule(EditorContext.getFile(this.editor));
      this.schedule(100);
    }
  }

  public static void syncFile(final IEditorPart editor) {
    try {
      EditorContext.getFile(editor)
      .refreshLocal(IResource.DEPTH_INFINITE, null);
    }
    catch (final Exception e) {
      EditorContext.LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
  }

  static IFile getFile(final IEditorPart editor) {
    return ((FileEditorInput) editor.getEditorInput()).getFile();
  }

  public static int getSaveIntervalInMilliSeconds() {
    return (EditorContext.getSaveIntervalInSeconds() * EditorContext.MILLI_SECONDS_UNIT)
        - EditorContext.SAVE_INTERVAL_DIFFERENCIAL;
  }

  public static int getSaveIntervalInSeconds() {
    return EditorContext.PREFERENCE.saveIntervalInSeconds();
  }

  public static void cancelAllJobs() {
    EditorContext.cancelJobsBelongingTo(EditorContext.LISTENER_TASK,
      EditorContext.SCHEDULED_SAVER_TASK);
    EditorContext.cancelJobsBelongingTo(EditorContext.LISTENER_SAVER_TASK,
      EditorContext.SCHEDULER_ENABLE_SAVE_LISTENERS_TASK);
    EditorContext.cancelJobsBelongingTo(EditorContext.FILE_SYNCER_TASK,
      EditorContext.AUTOMATIC_SAVER_TASK);
  }

  public static void cancelJobsBelongingTo(final String... jobNames) {
    for (final String jobName : jobNames)
      EditorContext.JOB_MANAGER.cancel(jobName);
  }

  public static void scheduleSave() {
    EditorContext.asyncScheduleSave(EditorContext.SCHEDULED_SAVER_TASK,
      EditorContext.SHORT_DELAY_TIME);
  }

  public static void scheduleSave(final int delayTime) {
    EditorContext.asyncScheduleSave(EditorContext.SCHEDULED_SAVER_TASK,
      delayTime);
  }

  private static void asyncScheduleSave(final String taskName,
    final int delayTime) {
    new Task(taskName, delayTime) {

      @Override
      public boolean shouldSchedule() {
        return EditorContext
            .taskDoesNotExist(EditorContext.SCHEDULED_SAVER_TASK);
      }

      @Override
      public boolean shouldRun() {
        return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
      }

      @Override
      public void execute() {
        EventBus.post(new ScheduleSaveEvent());
      }
    }.begin();
  }

  public static boolean taskDoesNotExist(final String... names) {
    for (final String name : names)
      if (EditorContext.JOB_MANAGER.find(name).length > 0) return false;
    return true;
  }
}
