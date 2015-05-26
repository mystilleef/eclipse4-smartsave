package com.laboki.eclipse.plugin.smartsave.contexts;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.main.ForceSaveJob;
import com.laboki.eclipse.plugin.smartsave.main.SaveJob;
import com.laboki.eclipse.plugin.smartsave.main.Scheduler;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public enum SaveContext {
	INSTANCE;

	static final SaveJob SAVER = new SaveJob();
	static final ForceSaveJob FORCE_SAVER = new ForceSaveJob();

	public static void
	save(final Optional<IEditorPart> editor) {
		try {
			if (SaveContext.canSave(editor)) SaveContext.execute(editor);
		}
		catch (final Exception e) {
			SaveContext.execute(editor);
		}
	}

	private static boolean
	canSave(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return EditorContext.canSaveAutomatically(editor)
			&& SaveContext.canSaveFile(editor);
	}

	private static boolean
	canSaveFile(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return !(SaveContext.isNotModified(editor)
			|| SaveContext.isBeingEdited(editor) || SaveContext.hasProblems(editor));
	}

	public static boolean
	isNotModified(final Optional<IEditorPart> editor) {
		return !SaveContext.isModified(editor);
	}

	public static boolean
	isModified(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return editor.get().isDirty();
	}

	private static boolean
	isBeingEdited(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return SaveContext.hasSelection(editor)
			|| AnnotationContext.isInLinkMode(editor);
	}

	public static boolean
	hasSelection(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		final Optional<StyledText> buffer = EditorContext.getBuffer(editor);
		if (!buffer.isPresent()) return false;
		return (buffer.get().getSelectionCount() > 0)
			|| buffer.get().getBlockSelection();
	}

	private static boolean
	hasProblems(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		return SaveContext.bufferHasErrors(editor)
			|| SaveContext.bufferHasWarnings(editor);
	}

	private static boolean
	bufferHasErrors(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		if (SaveContext.canSaveIfErrors()) return false;
		return AnnotationContext.hasErrors(editor);
	}

	private static boolean
	canSaveIfErrors() {
		return Store.getCanSaveIfErrors();
	}

	private static boolean
	bufferHasWarnings(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		if (SaveContext.canSaveIfWarnings()) return false;
		return AnnotationContext.hasWarnings(editor);
	}

	private static boolean
	canSaveIfWarnings() {
		return Store.getCanSaveIfWarnings();
	}

	private static void
	execute(final Optional<IEditorPart> editor) {
		new Task() {

			@Override
			public void
			execute() {
				SaveContext.SAVER.execute(editor);
			}
		}.setRule(Scheduler.RULE)
			.setDelay(Scheduler.DELAY)
			.setFamily(Scheduler.FAMILY)
			.start();
	}

	public static void
	forceSave(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return;
		if (!EditorContext.canSaveAutomatically(editor)) return;
		SaveContext.startForceSaveTask(editor);
	}

	private static void
	startForceSaveTask(final Optional<IEditorPart> editor) {
		new Task() {

			@Override
			public void
			execute() {
				SaveContext.FORCE_SAVER.execute(editor);
			}
		}.setFamily("").start();
	}
}
