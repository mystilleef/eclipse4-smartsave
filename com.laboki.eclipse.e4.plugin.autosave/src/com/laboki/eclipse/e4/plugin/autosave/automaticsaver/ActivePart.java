package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import java.util.Iterator;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.laboki.eclipse.e4.plugin.autosave.AddonMetadata;
import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.Preferences;

final class ActivePart {

	private static final String ANNOTATION_SEVERITY_WARNING = "warning";
	private static final String ANNOTATION_SEVERITY_ERROR = "error";
	private static ActivePart instance;

	private ActivePart() {
		Preferences.initialize();
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

	public static void flushEvents() {
		final Display display = ActivePart.getDisplay();
		while (display.readAndDispatch()) {}
		display.update();
	}

	static IEditorPart getEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	static StyledText getBuffer() {
		return (StyledText) ActivePart.getEditor().getAdapter(Control.class);
	}

	static StyledText getBuffer(final IEditorPart editor) {
		return (StyledText) editor.getAdapter(Control.class);
	}

	static SourceViewer getView() {
		return (SourceViewer) ActivePart.getEditor().getAdapter(ITextOperationTarget.class);
	}

	static SourceViewer getView(final IEditorPart editor) {
		return (SourceViewer) editor.getAdapter(ITextOperationTarget.class);
	}

	static void save() {
		ActivePart.flushEvents();
		ActivePart.getEditor().doSave(null);
		ActivePart.flushEvents();
	}

	static void save(final IEditorPart editor) {
		ActivePart.flushEvents();
		editor.doSave(null);
		ActivePart.flushEvents();
	}

	static boolean isModified() {
		return ActivePart.getEditor().isDirty();
	}

	static boolean isModified(final IEditorPart editor) {
		return editor.isDirty();
	}

	static boolean hasWarnings() {
		return ActivePart.getAnnotationSeverity(ActivePart.ANNOTATION_SEVERITY_WARNING);
	}

	static boolean hasWarnings(final IEditorPart editor) {
		return ActivePart.getAnnotationSeverity(ActivePart.ANNOTATION_SEVERITY_WARNING, editor);
	}

	static boolean hasErrors() {
		return ActivePart.getAnnotationSeverity(ActivePart.ANNOTATION_SEVERITY_ERROR);
	}

	static boolean hasErrors(final IEditorPart editor) {
		return ActivePart.getAnnotationSeverity(ActivePart.ANNOTATION_SEVERITY_ERROR, editor);
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

	static boolean canSaveIfWarnings() {
		return Preferences.canSaveIfWarnings();
	}

	static boolean canSaveIfErrors() {
		return Preferences.canSaveIfErrors();
	}

	static boolean canSaveAutomatically() {
		return Preferences.canSaveAutomatically();
	}

	static int getSaveIntervalInSeconds() {
		return Preferences.saveIntervalInSeconds();
	}

	static boolean isInvalid(final MPart activePart) {
		if (ActivePart.isNotAnEditor(activePart)) return true;
		if (ActivePart.isTagged(activePart)) return true;
		return false;
	}

	static boolean isNotAnEditor(final MPart activePart) {
		if (activePart == null) return true;
		if (activePart.getTags().contains("Editor")) return false;
		return true;
	}

	static boolean isTagged(final MPart activePart) {
		if (activePart == null) return true;
		if (activePart.getContext().containsKey(AddonMetadata.PLUGIN_NAME)) return true;
		return false;
	}

	static boolean isNotTagged(final MPart activePart) {
		return !ActivePart.isTagged(activePart);
	}
}
