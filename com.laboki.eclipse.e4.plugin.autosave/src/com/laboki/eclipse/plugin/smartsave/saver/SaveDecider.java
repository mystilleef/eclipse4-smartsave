package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

final class SaveDecider {

	private final IEditorPart editorPart = ActivePart.getEditor();

	public SaveDecider() {}

	public void save() {
		if (!ActivePart.canSaveAutomatically() || !this.canSaveFile()) return;
		ActivePart.save(this.editorPart);
	}

	private boolean canSaveFile() {
		if (this.bufferIsNotModified()) return false;
		if (this.bufferIsInEditingMode()) return false;
		if (this.bufferHasProblems()) return false;
		return true;
	}

	private boolean bufferIsNotModified() {
		return !ActivePart.isModified(this.editorPart);
	}

	private boolean bufferIsInEditingMode() {
		if (this.bufferHasSelection()) return true;
		if (this.bufferIsInLinkedMode()) return true;
		return false;
	}

	private boolean bufferHasSelection() {
		if (ActivePart.getBuffer(this.editorPart).getSelectionCount() != 0) return true;
		if (ActivePart.getBuffer(this.editorPart).getBlockSelection()) return true;
		return false;
	}

	private boolean bufferIsInLinkedMode() {
		return ActivePart.getLinkedMode(this.editorPart);
	}

	private boolean bufferHasProblems() {
		if (this.bufferHasErrors()) return true;
		if (this.bufferHasWarnings()) return true;
		return false;
	}

	private boolean bufferHasErrors() {
		if (ActivePart.canSaveIfErrors()) return false;
		return ActivePart.hasErrors(this.editorPart);
	}

	private boolean bufferHasWarnings() {
		if (ActivePart.canSaveIfWarnings()) return false;
		return ActivePart.hasWarnings(this.editorPart);
	}

	@Override
	public String toString() {
		return String.format("SaveDecider [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
