// $codepro.audit.disable largeNumberOfMethods, com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.minimizeScopeOfLocalVariables, debuggingCode
package com.laboki.eclipse.plugin.smartsave.automaticsaver;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.laboki.eclipse.plugin.smartsave.AddonMetadata;
import com.laboki.eclipse.plugin.smartsave.automaticsaver.preferences.Preference;

public final class ActivePart {

	private static ActivePart instance;
	private static final String ANNOTATION_SEVERITY_WARNING = "warning";
	private static final String ANNOTATION_SEVERITY_ERROR = "error";
	private static final String ANNOTATION_LINK_MODE_EXIT = "org.eclipse.ui.internal.workbench.texteditor.link.exit";
	private static final String ANNOTATION_LINK_MODE_TARGET = "org.eclipse.ui.internal.workbench.texteditor.link.target";
	private static final String ANNOTATION_LINK_MODE_MASTER = "org.eclipse.ui.internal.workbench.texteditor.link.master";
	private static final String ANNOTATION_LINK_MODE_SLAVE = "org.eclipse.ui.internal.workbench.texteditor.link.slave";
	private static final Display DISPLAY = ActivePart.getDisplay();

	private ActivePart() {
		Preference.initialize();
	}

	public static synchronized ActivePart initialize() {
		if (ActivePart.instance == null) ActivePart.instance = new ActivePart();
		return ActivePart.instance;
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) display = Display.getDefault();
		return display;
	}

	public static void asyncExec(final Runnable runnable) {
		ActivePart.DISPLAY.asyncExec(runnable);
	}

	public static void flushEvents() {
		while (ActivePart.DISPLAY.readAndDispatch())
			ActivePart.DISPLAY.update();
		ActivePart.DISPLAY.update();
	}

	public static IEditorPart getEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	public static StyledText getBuffer() {
		return (StyledText) ActivePart.getEditor().getAdapter(Control.class);
	}

	public static StyledText getBuffer(final IEditorPart editor) {
		return (StyledText) editor.getAdapter(Control.class);
	}

	public static SourceViewer getView() {
		return (SourceViewer) ActivePart.getEditor().getAdapter(ITextOperationTarget.class);
	}

	public static SourceViewer getView(final IEditorPart editor) {
		return (SourceViewer) editor.getAdapter(ITextOperationTarget.class);
	}

	public static void save() {
		ActivePart.flushEvents();
		ActivePart.getEditor().doSave(null);
		ActivePart.flushEvents();
	}

	public static void save(final IEditorPart editor) {
		ActivePart.flushEvents();
		editor.doSave(null);
		ActivePart.flushEvents();
	}

	public static boolean isModified() {
		return ActivePart.getEditor().isDirty();
	}

	public static boolean isModified(final IEditorPart editor) {
		return editor.isDirty();
	}

	public static boolean getLinkedMode(final IEditorPart editor) {
		ActivePart.syncFile(editor);
		return ActivePart.hasLinkedAnnotations(editor);
	}

	private static boolean hasLinkedAnnotations(final IEditorPart editor) {
		final Iterator<Annotation> iterator = ActivePart.getView(editor).getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (ActivePart.isInLinkedMode(iterator)) return true;
		return false;
	}

	private static boolean isInLinkedMode(final Iterator<Annotation> iterator) {
		final String annotationType = iterator.next().getType();
		if (annotationType.equals(ActivePart.ANNOTATION_LINK_MODE_EXIT)) return true;
		if (annotationType.equals(ActivePart.ANNOTATION_LINK_MODE_MASTER)) return true;
		if (annotationType.equals(ActivePart.ANNOTATION_LINK_MODE_SLAVE)) return true;
		if (annotationType.equals(ActivePart.ANNOTATION_LINK_MODE_TARGET)) return true;
		return false;
	}

	public static boolean hasWarnings() {
		return ActivePart.getAnnotationSeverity(ActivePart.ANNOTATION_SEVERITY_WARNING);
	}

	public static boolean hasWarnings(final IEditorPart editor) {
		ActivePart.syncFile(editor);
		return ActivePart.getAnnotationSeverity(ActivePart.ANNOTATION_SEVERITY_WARNING, editor);
	}

	public static boolean hasErrors() {
		return ActivePart.getAnnotationSeverity(ActivePart.ANNOTATION_SEVERITY_ERROR);
	}

	public static boolean hasErrors(final IEditorPart editor) {
		ActivePart.syncFile(editor);
		return ActivePart.getAnnotationSeverity(ActivePart.ANNOTATION_SEVERITY_ERROR, editor);
	}

	private static void syncFile(final IEditorPart editor) {
		try {
			ActivePart.flushEvents();
			ActivePart.getFile(editor).refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (final CoreException e) {
			e.printStackTrace();
		} finally {
			ActivePart.flushEvents();
		}
	}

	private static IFile getFile(final IEditorPart editor) {
		return ((FileEditorInput) editor.getEditorInput()).getFile();
	}

	private static boolean getAnnotationSeverity(final String problemSeverity) {
		final Iterator<Annotation> iterator = ActivePart.getView().getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (ActivePart.hasProblems(problemSeverity, iterator)) return true;
		return false;
	}

	private static boolean getAnnotationSeverity(final String problemSeverity, final IEditorPart editor) {
		final Iterator<Annotation> iterator = ActivePart.getView(editor).getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (ActivePart.hasProblems(problemSeverity, iterator)) return true;
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

	public static boolean isInvalid(final MPart activePart) {
		if (ActivePart.isNotAnEditor(activePart)) return true;
		if (ActivePart.isTagged(activePart)) return true;
		return false;
	}

	public static boolean isNotAnEditor(final MPart activePart) {
		if (activePart == null) return true;
		if (activePart.getTags().contains("Editor")) return false;
		return true;
	}

	public static boolean isTagged(final MPart activePart) {
		if (activePart == null) return true;
		if (activePart.getContext().containsKey(AddonMetadata.PLUGIN_NAME)) return true;
		return false;
	}

	public static boolean isNotTagged(final MPart activePart) {
		return !ActivePart.isTagged(activePart);
	}

	@Override
	public String toString() {
		return String.format("ActivePart [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
