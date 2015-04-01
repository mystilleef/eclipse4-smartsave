package com.laboki.eclipse.plugin.smartsave.main;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.smartsave.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.preferences.Cache;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public enum EditorContext {
  INSTANCE;

  public static final String PLUGIN_NAME =
    "com.laboki.eclipse.plugin.smartsave";
  public static final String CONTRIBUTOR_URI = MessageFormat.format(
    "plugin://{0}", EditorContext.PLUGIN_NAME);
  public static final String CONTRIBUTION_URI = "bundleclass://{0}/{1}";
  public static final int SHORT_DELAY = 250;
  public static final IJobManager JOB_MANAGER = Job.getJobManager();
  public static final IWorkbench WORKBENCH = PlatformUI.getWorkbench();
  public static final Display DISPLAY = EditorContext.WORKBENCH.getDisplay();
  public static final MessageConsole CONSOLE = EditorContext
      .getConsole("Smart Save");
  public static final String SAVER_TASK_FAMILY = "SAVER_TASK_FAMILY";
  public static final ISchedulingRule SAVER_TASK_RULE = new TaskMutexRule();
  static final SaveJob SAVE_JOB = new SaveJob();
  private static final String SAVER_TASK = "EDITOR_CONTEXT_SAVER_TASK";
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
  private final static DefaultMarkerAnnotationAccess ANNOTATION_ACCESS =
      new DefaultMarkerAnnotationAccess();
  private static final Logger LOGGER = Logger.getLogger(EditorContext.class
    .getName());

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

  public static void save(final IEditorPart editor) {
    try {
      if (EditorContext.canSave(editor)) EditorContext.tryToSave(editor);
    }
    catch (final Exception e) {
      EditorContext.tryToSave(editor);
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
    new AsyncTask() {

      @Override
      public void execute() {
        EditorContext.WORKBENCH.saveAllEditors(false);
      }
    }.begin();
  }

  public static void tryToSave(final IEditorPart editor) {
    new Task() {

      @Override
      protected boolean shouldSchedule() {
        return EditorContext.canScheduleSave();
      }

      @Override
      public void execute() {
        EditorContext.SAVE_JOB.execute(editor);
      }
    }.setFamily(EditorContext.SAVER_TASK_FAMILY).begin();
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

  public static void scheduleSave() {
    EditorContext.asyncScheduleSave(EditorContext.SAVER_TASK,
      EditorContext.SHORT_DELAY);
  }

  public static void scheduleSave(final int delayTime) {
    EditorContext.asyncScheduleSave(EditorContext.SAVER_TASK, delayTime);
  }

  private static void asyncScheduleSave(final String taskName,
    final int delayTime) {
    new Task() {

      @Override
      protected boolean shouldSchedule() {
        return EditorContext.canScheduleSave();
      }

      @Override
      public void execute() {
        EventBus.post(new ScheduleSaveEvent());
      }
    }.setName(taskName)
      .setFamily(EditorContext.SAVER_TASK_FAMILY)
      .setDelay(delayTime)
      .setRule(EditorContext.SAVER_TASK_RULE)
      .begin();
  }

  public static boolean taskDoesNotExist(final String... names) {
    for (final String name : names)
      if (EditorContext.JOB_MANAGER.find(name).length > 0) return false;
    return true;
  }

  public static boolean saveJobExists() {
    return EditorContext.JOB_MANAGER.find(SaveJob.JOB_FAMILY).length > 0;
  }

  public static void out(final Object message) {
    EditorContext.CONSOLE.newMessageStream().println(String.valueOf(message));
  }

  public static void showPluginConsole() {
    try {
      EditorContext.tryToShowConsole();
    }
    catch (final PartInitException e) {
      EditorContext.LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
  }

  private static void tryToShowConsole() throws PartInitException {
    ((IConsoleView) EditorContext.WORKBENCH.getActiveWorkbenchWindow()
        .getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW))
        .display(EditorContext.CONSOLE);
  }

  private static MessageConsole getConsole(final String name) {
    final MessageConsole console = EditorContext.findConsole(name);
    if (console != null) return console;
    return EditorContext.newConsole(name);
  }

  private static MessageConsole findConsole(final String name) {
    final IConsole[] consoles =
      ConsolePlugin.getDefault().getConsoleManager().getConsoles();
    for (final IConsole console : consoles)
      if (name.equals(console.getName())) return (MessageConsole) console;
    return null;
  }

  private static MessageConsole newConsole(final String name) {
    final MessageConsole myConsole = new MessageConsole(name, null);
    ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {
      myConsole
    });
    return myConsole;
  }

  public static void asyncExec(final Runnable runnable) {
    if (EditorContext.displayExists()) EditorContext.DISPLAY
      .asyncExec(runnable);
  }

  public static void syncExec(final Runnable runnable) {
    if (EditorContext.displayExists()) EditorContext.DISPLAY.syncExec(runnable);
  }

  private static boolean displayExists() {
    return !EditorContext.displayIsDisposed();
  }

  private static boolean displayIsDisposed() {
    if (EditorContext.DISPLAY == null) return true;
    return EditorContext.DISPLAY.isDisposed();
  }

  public static boolean hasNoSaverTaskJobs() {
    return Job.getJobManager().find(EditorContext.SAVER_TASK_FAMILY).length == 0;
  }

  public static void cancelAllSaverTasks() {
    EditorContext.JOB_MANAGER.cancel(EditorContext.SAVER_TASK_FAMILY);
    EditorContext.JOB_MANAGER.cancel(SaveJob.JOB_FAMILY);
  }

  public static boolean canScheduleSave() {
    return EditorContext.taskDoesNotExist(EditorContext.SAVER_TASK_FAMILY)
        || SaveJob.doesNotExists();
  }
}
