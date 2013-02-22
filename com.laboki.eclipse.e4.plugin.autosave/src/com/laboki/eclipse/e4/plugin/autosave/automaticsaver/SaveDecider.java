package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

final class SaveDecider {

	private final EditorContext editor = new EditorContext();

	void save() {
		if (this.canSaveFile()) this.editor.save();
	}

	private boolean canSaveFile() {
		if (this.bufferIsNotModified()) return false;
		if (this.hasWarnings()) return false;
		if (this.hasErrors()) return false;
		return true;
	}

	private boolean bufferIsNotModified() {
		return !this.editor.isModified();
	}

	private boolean hasWarnings() {
		return EditorContext.canCheckWarnings() && this.editor.hasWarnings();
	}

	private boolean hasErrors() {
		return EditorContext.canCheckErrors() && this.editor.hasErrors();
	}
}
