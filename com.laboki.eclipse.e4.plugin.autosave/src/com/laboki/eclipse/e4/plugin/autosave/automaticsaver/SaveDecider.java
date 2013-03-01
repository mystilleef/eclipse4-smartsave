package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.ui.IEditorPart;

final class SaveDecider {

	private final IEditorPart editorPart = ActivePart.getEditor();

	SaveDecider() {}

	void save() {
		if (this.hasSelection()) return;
		if (!ActivePart.canSaveAutomatically() || !this.canSaveFile()) return;
		ActivePart.save(this.editorPart);
	}

	private boolean hasSelection() {
		if (ActivePart.getBuffer(this.editorPart).getSelectionCount() != 0) return true;
		if (ActivePart.getBuffer(this.editorPart).getBlockSelection()) return true;
		return false;
	}

	private boolean canSaveFile() {
		if (this.bufferIsNotModified()) return false;
		if (this.hasWarnings()) return false;
		if (this.hasErrors()) return false;
		return true;
	}

	private boolean bufferIsNotModified() {
		return !ActivePart.isModified(this.editorPart);
	}

	private boolean hasWarnings() {
		if (ActivePart.canSaveIfWarnings()) return false;
		return ActivePart.hasWarnings(this.editorPart);
	}

	private boolean hasErrors() {
		if (ActivePart.canSaveIfErrors()) return false;
		return ActivePart.hasErrors(this.editorPart);
	}
}
