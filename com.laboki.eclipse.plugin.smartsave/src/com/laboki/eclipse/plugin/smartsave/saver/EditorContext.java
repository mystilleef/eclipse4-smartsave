// $codepro.audit.disable methodChainLength
package com.laboki.eclipse.plugin.smartsave.saver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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

import com.laboki.eclipse.plugin.smartsave.saver.preferences.Preference;

public final class EditorContext {

	private static EditorContext instance;
	private static final String ANNOTATION_SEVERITY_WARNING = "warning";
	private static final String ANNOTATION_SEVERITY_ERROR = "error";
	private static final List<String> LINK_ANNOTATIONS = new ArrayList<>(Arrays.asList("org.eclipse.ui.internal.workbench.texteditor.link.exit", "org.eclipse.ui.internal.workbench.texteditor.link.target", "org.eclipse.ui.internal.workbench.texteditor.link.master", "org.eclipse.ui.internal.workbench.texteditor.link.slave"));
	private static final Display DISPLAY = EditorContext.getDisplay();
	private static final Preference PREFERENCE = Preference.instance();

	private EditorContext() {}

	public static IPartService getIPartService() {
		return (IPartService) PlatformUI.getWorkbench().getService(IPartService.class);
	}

	public static synchronized EditorContext instance() {
		if (EditorContext.instance == null) EditorContext.instance = new EditorContext();
		return EditorContext.instance;
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) display = Display.getDefault();
		return display;
	}

	public static void asyncExec(final Runnable runnable) {
		EditorContext.DISPLAY.asyncExec(runnable);
	}

	public static void flushEvents() {
		while (EditorContext.DISPLAY.readAndDispatch())
			EditorContext.DISPLAY.update();
		EditorContext.DISPLAY.update();
	}

	public static IEditorPart getEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	public static StyledText getBuffer() {
		return (StyledText) EditorContext.getEditor().getAdapter(Control.class);
	}

	public static StyledText getBuffer(final IEditorPart editor) {
		return (StyledText) editor.getAdapter(Control.class);
	}

	public static boolean hasSelection(final IEditorPart editor) {
		return EditorContext.getBuffer(editor).getSelectionCount() > 0;
	}

	public static boolean hasBlockSelection(final IEditorPart editor) {
		return EditorContext.getBuffer(editor).getBlockSelection();
	}

	public static SourceViewer getView() {
		return (SourceViewer) EditorContext.getEditor().getAdapter(ITextOperationTarget.class);
	}

	public static SourceViewer getView(final IEditorPart editor) {
		return (SourceViewer) editor.getAdapter(ITextOperationTarget.class);
	}

	public static void save() {
		EditorContext.flushEvents();
		EditorContext.getEditor().doSave(null);
		EditorContext.flushEvents();
	}

	public static void save(final IEditorPart editor) {
		EditorContext.flushEvents();
		editor.doSave(null);
		EditorContext.flushEvents();
	}

	public static boolean isModified() {
		return EditorContext.getEditor().isDirty();
	}

	public static boolean isModified(final IEditorPart editor) {
		return editor.isDirty();
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

	public static boolean hasWarnings() {
		return EditorContext.getAnnotationSeverity(EditorContext.ANNOTATION_SEVERITY_WARNING);
	}

	public static boolean hasWarnings(final IEditorPart editor) {
		EditorContext.syncFile(editor);
		return EditorContext.getAnnotationSeverity(EditorContext.ANNOTATION_SEVERITY_WARNING, editor);
	}

	public static boolean hasErrors() {
		return EditorContext.getAnnotationSeverity(EditorContext.ANNOTATION_SEVERITY_ERROR);
	}

	public static boolean hasErrors(final IEditorPart editor) {
		EditorContext.syncFile(editor);
		return EditorContext.getAnnotationSeverity(EditorContext.ANNOTATION_SEVERITY_ERROR, editor);
	}

	private static void syncFile(final IEditorPart editor) {
		EditorContext.flushEvents();
		EditorContext.tryToSyncFile(editor);
	}

	private static void tryToSyncFile(final IEditorPart editor) {
		try {
			EditorContext.getFile(editor).refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (final CoreException e) {
			e.printStackTrace();
		} finally {
			EditorContext.flushEvents();
		}
	}

	private static IFile getFile(final IEditorPart editor) {
		return ((FileEditorInput) editor.getEditorInput()).getFile();
	}

	private static boolean getAnnotationSeverity(final String problemSeverity) {
		final Iterator<Annotation> iterator = EditorContext.getView().getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (EditorContext.hasProblems(problemSeverity, iterator)) return true;
		return false;
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

	public static boolean canSaveAutomatically() {
		return EditorContext.PREFERENCE.canSaveAutomatically();
	}

	public static int getSaveIntervalInSeconds() {
		return EditorContext.PREFERENCE.saveIntervalInSeconds();
	}

	@Override
	public String toString() {
		return String.format("EditorContext [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
