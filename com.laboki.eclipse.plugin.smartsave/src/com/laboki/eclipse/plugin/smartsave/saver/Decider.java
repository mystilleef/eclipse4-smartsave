package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

final class Decider {

	private static final EditorContext EDITOR = EditorContext.instance();
	private final IEditorPart editor = EditorContext.getEditor();

	public Decider() {}

	public void save() {
		if (!Decider.EDITOR.canSaveAutomatically() || !this.canSaveFile()) return;
		EditorContext.save(this.editor);
	}

	private boolean canSaveFile() {
		if (this.bufferIsNotModified()) return false;
		if (this.bufferIsInEditingMode()) return false;
		if (this.bufferHasProblems()) return false;
		return true;
	}

	private boolean bufferIsNotModified() {
		return !EditorContext.isModified(this.editor);
	}

	private boolean bufferIsInEditingMode() {
		if (this.bufferHasSelection()) return true;
		if (this.bufferIsInLinkMode()) return true;
		return false;
	}

	private boolean bufferHasSelection() {
		if (EditorContext.getBuffer(this.editor).getSelectionCount() != 0) return true;
		if (EditorContext.getBuffer(this.editor).getBlockSelection()) return true;
		return false;
	}

	private boolean bufferIsInLinkMode() {
		return EditorContext.isInLinkMode(this.editor);
	}

	private boolean bufferHasProblems() {
		if (this.bufferHasErrors()) return true;
		if (this.bufferHasWarnings()) return true;
		return false;
	}

	private boolean bufferHasErrors() {
		if (Decider.EDITOR.canSaveIfErrors()) return false;
		return EditorContext.hasErrors(this.editor);
	}

	private boolean bufferHasWarnings() {
		if (Decider.EDITOR.canSaveIfWarnings()) return false;
		return EditorContext.hasWarnings(this.editor);
	}

	@Override
	public String toString() {
		return String.format("Decider [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
