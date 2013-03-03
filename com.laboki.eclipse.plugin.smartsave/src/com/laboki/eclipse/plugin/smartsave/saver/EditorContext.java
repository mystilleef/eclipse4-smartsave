// $codepro.audit.disable largeNumberOfMethods, com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.minimizeScopeOfLocalVariables, debuggingCode
package com.laboki.eclipse.plugin.smartsave.saver;

import java.util.Iterator;

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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.laboki.eclipse.plugin.smartsave.saver.preferences.Preference;

public final class EditorContext {

	private static EditorContext instance;
	private static final String ANNOTATION_SEVERITY_WARNING = "warning";
	private static final String ANNOTATION_SEVERITY_ERROR = "error";
	private static final String ANNOTATION_LINK_MODE_EXIT = "org.eclipse.ui.internal.workbench.texteditor.link.exit";
	private static final String ANNOTATION_LINK_MODE_TARGET = "org.eclipse.ui.internal.workbench.texteditor.link.target";
	private static final String ANNOTATION_LINK_MODE_MASTER = "org.eclipse.ui.internal.workbench.texteditor.link.master";
	private static final String ANNOTATION_LINK_MODE_SLAVE = "org.eclipse.ui.internal.workbench.texteditor.link.slave";
	private static final Display DISPLAY = EditorContext.getDisplay();

	private EditorContext() {
		Preference.initialize();
	}

	public static synchronized EditorContext initialize() {
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
		final String annotationType = iterator.next().getType();
		if (annotationType.equals(EditorContext.ANNOTATION_LINK_MODE_EXIT)) return true;
		if (annotationType.equals(EditorContext.ANNOTATION_LINK_MODE_MASTER)) return true;
		if (annotationType.equals(EditorContext.ANNOTATION_LINK_MODE_SLAVE)) return true;
		if (annotationType.equals(EditorContext.ANNOTATION_LINK_MODE_TARGET)) return true;
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
		try {
			EditorContext.flushEvents();
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

	public static boolean canSaveIfWarnings() {
		return Preference.canSaveIfWarnings();
	}

	public static boolean canSaveIfErrors() {
		return Preference.canSaveIfErrors();
	}

	public static boolean canSaveAutomatically() {
		return Preference.canSaveAutomatically();
	}

	public static int getSaveIntervalInSeconds() {
		return Preference.saveIntervalInSeconds();
	}

	@Override
	public String toString() {
		return String.format("EditorContext [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
