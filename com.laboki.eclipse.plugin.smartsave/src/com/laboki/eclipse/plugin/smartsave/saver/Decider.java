package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

final class Decider {

	private final IEditorPart editorPart = EditorContext.getEditor();

	public Decider() {}

	public void save() {
		if (!EditorContext.canSaveAutomatically() || !this.canSaveFile()) return;
		EditorContext.save(this.editorPart);
	}

	private boolean canSaveFile() {
		if (this.bufferIsNotModified()) return false;
		if (this.bufferIsInEditingMode()) return false;
		if (this.bufferHasProblems()) return false;
		return true;
	}

	private boolean bufferIsNotModified() {
		return !EditorContext.isModified(this.editorPart);
	}

	private boolean bufferIsInEditingMode() {
		if (this.bufferHasSelection()) return true;
		if (this.bufferIsInLinkedMode()) return true;
		return false;
	}

	private boolean bufferHasSelection() {
		if (EditorContext.getBuffer(this.editorPart).getSelectionCount() != 0) return true;
		if (EditorContext.getBuffer(this.editorPart).getBlockSelection()) return true;
		return false;
	}

	private boolean bufferIsInLinkedMode() {
		return EditorContext.getLinkedMode(this.editorPart);
	}

	private boolean bufferHasProblems() {
		if (this.bufferHasErrors()) return true;
		if (this.bufferHasWarnings()) return true;
		return false;
	}

	private boolean bufferHasErrors() {
		if (EditorContext.canSaveIfErrors()) return false;
		return EditorContext.hasErrors(this.editorPart);
	}

	private boolean bufferHasWarnings() {
		if (EditorContext.canSaveIfWarnings()) return false;
		return EditorContext.hasWarnings(this.editorPart);
	}

	@Override
	public String toString() {
		return String.format("Decider [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
