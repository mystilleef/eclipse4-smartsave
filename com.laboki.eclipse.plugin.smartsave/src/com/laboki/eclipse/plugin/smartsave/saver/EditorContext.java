package com.laboki.eclipse.plugin.smartsave.saver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import lombok.extern.java.Log;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.laboki.eclipse.plugin.smartsave.DelayedTask;
import com.laboki.eclipse.plugin.smartsave.saver.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.saver.preferences.Preference;

@Log
public enum EditorContext {
	INSTANCE;

	public static final int SHORT_DELAY_TIME = 250;
	public static final int MEDIUM_SHORT_DELAY_TIME = 500;
	public static final int MEDIUM_LONG_DELAY_TIME = 750;
	public static final int LONG_DELAY_TIME = 1000;
	public static final String AUTOMATIC_SAVER_TASK = "Automatic saver task";
	public static final String SCHEDULED_SAVER_TASK = "Scheduled saver task";
	private static final String ANNOTATION_SEVERITY_WARNING = "warning";
	private static final String ANNOTATION_SEVERITY_ERROR = "error";
	private static final List<String> LINK_ANNOTATIONS = new ArrayList<>(Arrays.asList("org.eclipse.ui.internal.workbench.texteditor.link.exit", "org.eclipse.ui.internal.workbench.texteditor.link.target", "org.eclipse.ui.internal.workbench.texteditor.link.master", "org.eclipse.ui.internal.workbench.texteditor.link.slave"));
	private static final Preference PREFERENCE = Preference.instance();
	public static final Display DISPLAY = PlatformUI.getWorkbench().getDisplay();
	public static final IJobManager JOB_MANAGER = Job.getJobManager();

	public static Display getDisplay() {
		return EditorContext.DISPLAY;
	}

	public static void asyncExec(final Runnable runnable) {
		if ((EditorContext.DISPLAY == null) || EditorContext.DISPLAY.isDisposed()) return;
		EditorContext.DISPLAY.asyncExec(runnable);
	}

	public static void flushEvents() {}

	public static IEditorPart getEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	public static StyledText getBuffer(final IEditorPart editor) {
		return (StyledText) editor.getAdapter(Control.class);
	}

	public static boolean hasSelection(final IEditorPart editor) {
		return (EditorContext.getBuffer(editor).getSelectionCount() > 0) || EditorContext.getBuffer(editor).getBlockSelection();
	}

	public static boolean hasBlockSelection(final IEditorPart editor) {
		return EditorContext.getBuffer(editor).getBlockSelection();
	}

	public static SourceViewer getView(final IEditorPart editor) {
		return (SourceViewer) editor.getAdapter(ITextOperationTarget.class);
	}

	public static void save(final IEditorPart editor) {
		editor.getSite().getPage().saveEditor(editor, false);
	}

	public static boolean isModified(final IEditorPart editor) {
		return editor.isDirty();
	}

	public static boolean isNotModified(final IEditorPart editor) {
		return !EditorContext.isModified(editor);
	}

	public static boolean isInLinkMode(final IEditorPart editor) {
		EditorContext.syncFile(editor);
		return EditorContext.hasLinkAnnotations(editor);
	}

	private static boolean hasLinkAnnotations(final IEditorPart editor) {
		final Iterator<Annotation> iterator = EditorContext.getView(editor).getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (EditorContext.isLinkModeAnnotation(iterator)) return true;
		return false;
	}

	private static boolean isLinkModeAnnotation(final Iterator<Annotation> iterator) {
		if (EditorContext.LINK_ANNOTATIONS.contains(iterator.next().getType())) return true;
		return false;
	}

	public static boolean hasWarnings(final IEditorPart editor) {
		EditorContext.syncFile(editor);
		return EditorContext.getAnnotationSeverity(EditorContext.ANNOTATION_SEVERITY_WARNING, editor);
	}

	public static boolean hasErrors(final IEditorPart editor) {
		EditorContext.syncFile(editor);
		return EditorContext.getAnnotationSeverity(EditorContext.ANNOTATION_SEVERITY_ERROR, editor);
	}

	static void syncFile(@SuppressWarnings("unused") final IEditorPart editor) {}

	public static void tryToSyncFile(final IEditorPart editor) {
		try {
			// This massively slows down Eclipse.
			EditorContext.getFile(editor).refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (final Exception e) {
			EditorContext.log.log(Level.INFO, "Failed to sync IFile resource", e);
		}
	}

	private static IFile getFile(final IEditorPart editor) {
		return ((FileEditorInput) editor.getEditorInput()).getFile();
	}

	private static boolean getAnnotationSeverity(final String problemSeverity, final IEditorPart editor) {
		final Iterator<Annotation> iterator = EditorContext.getView(editor).getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (EditorContext.hasProblems(problemSeverity, iterator)) return true;
		return false;
	}

	private static boolean hasProblems(final String problemSeverity, final Iterator<Annotation> iterator) {
		return iterator.next().getType().endsWith(problemSeverity);
	}

	public static boolean hoversHaveFocus(final IEditorPart editor) {
		if (EditorContext.getView(editor).getCurrentTextHover() != null) return true;
		if (EditorContext.getView(editor).getCurrentAnnotationHover() != null) return true;
		return false;
	}

	public static boolean canSaveIfWarnings() {
		return EditorContext.PREFERENCE.canSaveIfWarnings();
	}

	public static boolean canSaveIfErrors() {
		return EditorContext.PREFERENCE.canSaveIfErrors();
	}

	public static boolean canNotSaveAutomatically() {
		return !EditorContext.canSaveAutomatically();
	}

	public static boolean canSaveAutomatically() {
		return EditorContext.PREFERENCE.canSaveAutomatically();
	}

	public static int getSaveIntervalInSeconds() {
		return EditorContext.PREFERENCE.saveIntervalInSeconds();
	}

	public static IPartService getPartService() {
		return (IPartService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IPartService.class);
	}

	public static void cancelAllJobs() {
		EditorContext.cancelSaveJobs();
		EditorContext.cancelScheduledSaveJobs();
	}

	public static void cancelSaveJobs() {
		EditorContext.cancelJobsBelongingTo(EditorContext.AUTOMATIC_SAVER_TASK);
	}

	public static void cancelScheduledSaveJobs() {
		EditorContext.cancelJobsBelongingTo(EditorContext.SCHEDULED_SAVER_TASK);
	}

	public static void cancelJobsBelongingTo(final String jobName) {
		EditorContext.JOB_MANAGER.cancel(jobName);
	}

	public static boolean isBusy() {
		return EditorContext.jobManagerIsBusy() || EditorContext.uiThreadIsBusy();
	}

	public static boolean jobManagerIsBusy() {
		return !EditorContext.jobManagerIsIdle();
	}

	public static boolean jobManagerIsIdle() {
		return EditorContext.JOB_MANAGER.isIdle();
	}

	public static boolean uiThreadIsBusy() {
		return EditorContext.DISPLAY.readAndDispatch();
	}

	public static void tryToSave(final IEditorPart editor) {
		if (EditorContext.canSaveFile(editor)) EditorContext.save(editor);
	}

	private static boolean canSaveFile(final IEditorPart editor) {
		if (EditorContext.canNotSaveAutomatically() || EditorContext.isNotModified(editor)) return false;
		if (EditorContext.hasSelection(editor) || EditorContext.isInLinkMode(editor)) return false;
		if (EditorContext.bufferHasErrors(editor) || EditorContext.bufferHasWarnings(editor)) return false;
		return true;
	}

	private static boolean bufferHasErrors(final IEditorPart editor) {
		if (EditorContext.canSaveIfErrors()) return false;
		return EditorContext.hasErrors(editor);
	}

	private static boolean bufferHasWarnings(final IEditorPart editor) {
		if (EditorContext.canSaveIfWarnings()) return false;
		return EditorContext.hasWarnings(editor);
	}

	public static void scheduleSave(final EventBus eventBus) {
		EditorContext.cancelAllJobs();
		EditorContext.asyncExec(new DelayedTask(EditorContext.SCHEDULED_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

			@Override
			public void execute() {
				eventBus.post(new ScheduleSaveEvent());
			}
		});
	}
}
