package com.laboki.eclipse.plugin.smartsave.main;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.laboki.eclipse.plugin.smartsave.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public enum EditorContext {
	INSTANCE;

	static final ForceSaveJob FORCE_SAVE_JOB = new ForceSaveJob();
	public static final String PLUGIN_NAME =
		"com.laboki.eclipse.plugin.smartsave";
	public static final String CONTRIBUTOR_URI =
		MessageFormat.format("plugin://{0}", EditorContext.PLUGIN_NAME);
	public static final String CONTRIBUTION_URI = "bundleclass://{0}/{1}";
	public static final int SHORT_DELAY = 250;
	public static final IJobManager JOB_MANAGER = Job.getJobManager();
	public static final IWorkbench WORKBENCH = PlatformUI.getWorkbench();
	public static final Display DISPLAY = EditorContext.WORKBENCH.getDisplay();
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
	private static final List<String> LINK_ANNOTATIONS =
		Lists.newArrayList(EditorContext.LINK_EXIT,
			EditorContext.LINK_TARGET,
			EditorContext.LINK_MASTER,
			EditorContext.LINK_SLAVE);
	private static final DefaultMarkerAnnotationAccess ANNOTATION_ACCESS =
		new DefaultMarkerAnnotationAccess();
	private static final Logger LOGGER =
		Logger.getLogger(EditorContext.class.getName());

	public static Optional<Shell>
	getShell() {
		return Optional.fromNullable(EditorContext.WORKBENCH.getModalDialogShellProvider()
			.getShell());
	}

	public static Optional<IPartService>
	getPartService() {
		final Optional<IWorkbenchWindow> window =
			EditorContext.getActiveWorkbenchWindow();
		if (!window.isPresent()) return Optional.absent();
		return Optional.fromNullable((IPartService) window.get()
			.getService(IPartService.class));
	}

	public static Optional<IEditorPart>
	getEditor() {
		final Optional<IWorkbenchWindow> window =
			EditorContext.getActiveWorkbenchWindow();
		if (!window.isPresent()) return Optional.absent();
		final Optional<IWorkbenchPage> page = EditorContext.getActivePage(window);
		if (!page.isPresent()) return Optional.absent();
		return Optional.fromNullable(page.get().getActiveEditor());
	}

	private static Optional<IWorkbenchWindow>
	getActiveWorkbenchWindow() {
		return Optional.fromNullable(EditorContext.WORKBENCH.getActiveWorkbenchWindow());
	}

	private static Optional<IWorkbenchPage>
	getActivePage(final Optional<IWorkbenchWindow> activeWorkbenchWindow) {
		return Optional.fromNullable(activeWorkbenchWindow.get().getActivePage());
	}

	public static Optional<Control>
	getControl(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		return Optional.fromNullable((Control) editor.get()
			.getAdapter(Control.class));
	}

	public static Optional<StyledText>
	getBuffer(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		final Optional<Control> control = EditorContext.getControl(editor);
		if (!control.isPresent()) return Optional.absent();
		return Optional.fromNullable((StyledText) EditorContext.getControl(editor)
			.get());
	}

	public static Optional<SourceViewer>
	getView(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		return Optional.fromNullable((SourceViewer) editor.get()
			.getAdapter(ITextOperationTarget.class));
	}

	public static void
	save(final Optional<IEditorPart> editor) {
		try {
			if (EditorContext.canSave(editor)) EditorContext.tryToSave(editor);
		}
		catch (final Exception e) {
			EditorContext.tryToSave(editor);
		}
	}

	private static boolean
	canSave(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return EditorContext.canSaveAutomatically(editor)
			&& EditorContext.canSaveFile(editor);
	}

	public static boolean
	canSaveAutomatically(final Optional<IEditorPart> editor) {
		if (!Store.getCanSaveAutomatically()) return false;
		if (EditorContext.isBlacklisted(editor)) return false;
		return true;
	}

	public static void
	toggleCanSaveAutomatically() {
		Store.toggleCanSaveAutomatically();
	}

	public static void
	setCanSaveAutomatically(final boolean canSave) {
		Store.setCanSaveAutomatically(canSave);
	}

	private static boolean
	canSaveFile(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return !(EditorContext.isNotModified(editor)
			|| EditorContext.isBeingEdited(editor) || EditorContext.hasProblems(editor));
	}

	private static boolean
	isBeingEdited(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return EditorContext.hasSelection(editor)
			|| EditorContext.isInLinkMode(editor);
	}

	private static boolean
	hasProblems(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return EditorContext.bufferHasErrors(editor)
			|| EditorContext.bufferHasWarnings(editor);
	}

	public static boolean
	isNotModified(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return !EditorContext.isModified(editor);
	}

	public static boolean
	isModified(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return editor.get().isDirty();
	}

	public static boolean
	hasSelection(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		final Optional<StyledText> buffer = EditorContext.getBuffer(editor);
		if (!buffer.isPresent()) return false;
		return (buffer.get().getSelectionCount() > 0)
			|| buffer.get().getBlockSelection();
	}

	public static boolean
	isInLinkMode(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return EditorContext.hasLinkAnnotations(editor);
	}

	private static boolean
	hasLinkAnnotations(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		final Optional<SourceViewer> view = EditorContext.getView(editor);
		if (!view.isPresent()) return false;
		final Iterator<Annotation> iterator =
			view.get().getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (EditorContext.isLinkModeAnnotation(iterator)) return true;
		return false;
	}

	private static boolean
	isLinkModeAnnotation(final Iterator<Annotation> iterator) {
		if (EditorContext.LINK_ANNOTATIONS.contains(iterator.next().getType())) return true;
		return false;
	}

	private static boolean
	bufferHasErrors(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		if (EditorContext.canSaveIfErrors()) return false;
		return EditorContext.hasErrors(editor);
	}

	public static boolean
	canSaveIfErrors() {
		return Store.getCanSaveIfErrors();
	}

	public static boolean
	hasErrors(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return EditorContext.getAnnotationSeverity(EditorContext.ANNOTATION_ERROR,
			editor);
	}

	private static boolean
	bufferHasWarnings(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		if (EditorContext.canSaveIfWarnings()) return false;
		return EditorContext.hasWarnings(editor);
	}

	public static boolean
	canSaveIfWarnings() {
		return Store.getCanSaveIfWarnings();
	}

	public static boolean
	hasWarnings(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return EditorContext.getAnnotationSeverity(EditorContext.ANNOTATION_WARNING,
			editor);
	}

	private static boolean
	getAnnotationSeverity(final String problemSeverity,
												final Optional<IEditorPart> editor) {
		final Optional<SourceViewer> view = EditorContext.getView(editor);
		if (!view.isPresent()) return false;
		final Iterator<Annotation> iterator =
			view.get().getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (EditorContext.hasProblems(problemSeverity, iterator)) return true;
		return false;
	}

	private static boolean
	hasProblems(final String problemSeverity, final Iterator<Annotation> iterator) {
		final Annotation annotation = iterator.next();
		if (annotation.isMarkedDeleted()) return false;
		return EditorContext.ANNOTATION_ACCESS.isSubtype(annotation.getType(),
			problemSeverity);
	}

	public static void
	forceSave(final Optional<IEditorPart> editor) {
		if (!EditorContext.canSaveAutomatically(editor)) return;
		EditorContext.startForceSaveTask(editor);
	}

	private static void
	startForceSaveTask(final Optional<IEditorPart> editor) {
		new Task() {

			@Override
			public void
			execute() {
				EditorContext.FORCE_SAVE_JOB.execute(editor);
			}
		}.start();
	}

	public static void
	tryToSave(final Optional<IEditorPart> editor) {
		new Task() {

			@Override
			protected boolean
			shouldSchedule() {
				return EditorContext.canScheduleSave();
			}

			@Override
			public void
			execute() {
				EditorContext.SAVE_JOB.execute(editor);
			}
		}.setFamily(EditorContext.SAVER_TASK_FAMILY).start();
	}

	static Optional<IFile>
	getFile(final Optional<IEditorPart> editor) {
		final Optional<FileEditorInput> fileEditorInput =
			EditorContext.getFileEditorInput(editor);
		if (!fileEditorInput.isPresent()) return Optional.absent();
		return Optional.fromNullable(fileEditorInput.get().getFile());
	}

	private static Optional<FileEditorInput>
	getFileEditorInput(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		final IEditorInput editorInput = editor.get().getEditorInput();
		if (!(editorInput instanceof FileEditorInput)) return Optional.absent();
		return Optional.fromNullable((FileEditorInput) editorInput);
	}

	public static int
	getSaveIntervalInMilliSeconds() {
		return (EditorContext.getSaveIntervalInSeconds() * EditorContext.MILLI_SECONDS_UNIT)
			- EditorContext.SAVE_INTERVAL_DIFFERENCIAL;
	}

	public static int
	getSaveIntervalInSeconds() {
		return Store.getSaveIntervalInSeconds();
	}

	public static void
	scheduleSave() {
		EditorContext.asyncScheduleSave(EditorContext.SAVER_TASK,
			EditorContext.SHORT_DELAY);
	}

	public static void
	scheduleSave(final int delayTime) {
		EditorContext.asyncScheduleSave(EditorContext.SAVER_TASK, delayTime);
	}

	private static void
	asyncScheduleSave(final String taskName, final int delayTime) {
		new Task() {

			@Override
			protected boolean
			shouldSchedule() {
				return EditorContext.canScheduleSave();
			}

			@Override
			public void
			execute() {
				EventBus.post(new ScheduleSaveEvent());
			}
		}.setName(taskName)
			.setFamily(EditorContext.SAVER_TASK_FAMILY)
			.setDelay(delayTime)
			.setRule(EditorContext.SAVER_TASK_RULE)
			.start();
	}

	public static boolean
	taskDoesNotExist(final String... names) {
		for (final String name : names)
			if (EditorContext.JOB_MANAGER.find(name).length > 0) return false;
		return true;
	}

	public static boolean
	saveJobExists() {
		return EditorContext.JOB_MANAGER.find(SaveJob.JOB_FAMILY).length > 0;
	}

	public static void
	asyncExec(final Runnable runnable) {
		if (EditorContext.displayDoesNotExist()) return;
		EditorContext.DISPLAY.asyncExec(runnable);
	}

	public static void
	syncExec(final Runnable runnable) {
		if (EditorContext.displayDoesNotExist()) return;
		EditorContext.DISPLAY.syncExec(runnable);
	}

	private static boolean
	displayDoesNotExist() {
		return !EditorContext.displayExists();
	}

	private static boolean
	displayExists() {
		return !EditorContext.displayIsDisposed();
	}

	private static boolean
	displayIsDisposed() {
		if (EditorContext.DISPLAY == null) return true;
		return EditorContext.DISPLAY.isDisposed();
	}

	public static boolean
	hasNoSaverTaskJobs() {
		return Job.getJobManager().find(EditorContext.SAVER_TASK_FAMILY).length == 0;
	}

	public static void
	cancelAllSaverTasks() {
		EditorContext.JOB_MANAGER.cancel(EditorContext.SAVER_TASK_FAMILY);
		EditorContext.JOB_MANAGER.cancel(SaveJob.JOB_FAMILY);
	}

	public static boolean
	canScheduleSave() {
		if (EditorContext.taskDoesNotExist(EditorContext.SAVER_TASK_FAMILY)) return true;
		return SaveJob.doesNotExists();
	}

	static boolean
	currentJobIsBlocking() {
		final Job currentJob = EditorContext.JOB_MANAGER.currentJob();
		if (currentJob == null) return false;
		return currentJob.isBlocking();
	}

	public static ArrayList<String>
	getBlacklist() {
		final String blacklist = Store.getContentTypeBlacklist();
		if (blacklist.isEmpty()) return new ArrayList<>();
		return new ArrayList<>(Arrays.asList(blacklist.split(";")));
	}

	public static void
	setBlacklist(final String contentTypes) {
		Store.setContentTypeBlacklist(contentTypes);
	}

	public static boolean
	isBlacklisted(final Optional<IEditorPart> editor) {
		final ArrayList<String> blacklist = EditorContext.getBlacklist();
		if (blacklist.isEmpty()) return false;
		if (blacklist.contains(EditorContext.getContentTypeId(editor))) return true;
		return false;
	}

	private static String
	getContentTypeId(final Optional<IEditorPart> editor) {
		final Optional<IContentType> contentType =
			EditorContext.getContentType(editor);
		if (contentType.isPresent()) return contentType.get().getId();
		return "";
	}

	private static Optional<IContentType>
	getContentType(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		final Optional<IContentDescription> description =
			EditorContext.getContentDescription(editor);
		if (!description.isPresent()) return Optional.absent();
		return Optional.fromNullable(description.get().getContentType());
	}

	private static Optional<IContentDescription>
	getContentDescription(final Optional<IEditorPart> editor) {
		try {
			return EditorContext.tryToGetContentDescription(editor);
		}
		catch (final CoreException e) {
			EditorContext.LOGGER.log(Level.WARNING, e.getMessage(), e);
			return Optional.absent();
		}
	}

	private static Optional<IContentDescription>
	tryToGetContentDescription(final Optional<IEditorPart> editor)
		throws CoreException {
		final Optional<IFile> file = EditorContext.getFile(editor);
		if (!file.isPresent()) return Optional.absent();
		return Optional.fromNullable(file.get().getContentDescription());
	}

	public static IContentType[]
	getContentTypes() {
		return Platform.getContentTypeManager().getAllContentTypes();
	}
}
