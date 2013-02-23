package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import java.util.Iterator;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.laboki.eclipse.e4.plugin.autosave.AddonMetadata;

final class ActivePart {

	public static final boolean CAN_CHECK_WARNINGS_DEFAULT_VALUE = false;
	public static final boolean CAN_CHECK_ERRORS_DEFAULT_VALUE = true;
	public static final boolean CAN_SAVE_AUTOMATICALLY_DEFAULT_VALUE = true;
	public static final int SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE = 5;

	private ActivePart() {}

	static IEditorPart getEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	static StyledText getBuffer() {
		return (StyledText) ActivePart.getEditor().getAdapter(Control.class);
	}

	static SourceViewer getView() {
		return (SourceViewer) ActivePart.getEditor().getAdapter(ITextOperationTarget.class);
	}

	static void save() {
		ActivePart.getEditor().doSave(null);
	}

	static boolean isModified() {
		return ActivePart.getEditor().isDirty();
	}

	static boolean hasWarnings() {
		return ActivePart.getAnnotationSeverity("warning");
	}

	static boolean hasErrors() {
		return ActivePart.getAnnotationSeverity("error");
	}

	private static boolean getAnnotationSeverity(final String problemSeverity) {
		final Iterator<Annotation> iterator = ActivePart.getView().getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (ActivePart.hasProblems(problemSeverity, iterator)) return true;
		return false;
	}

	private static boolean hasProblems(final String problemSeverity, final Iterator<Annotation> iterator) {
		return iterator.next().getType().endsWith(problemSeverity);
	}

	static boolean canCheckWarnings() {
		return false;
	}

	static boolean canCheckErrors() {
		return true;
	}

	static int getSaveIntervalInSeconds() {
		return ActivePart.SAVE_INTERVAL_IN_SECONDS_DEFAULT_VALUE;
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
