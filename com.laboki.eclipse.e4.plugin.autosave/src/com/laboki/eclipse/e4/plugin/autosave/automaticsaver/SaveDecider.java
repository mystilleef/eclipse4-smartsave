package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

final class SaveDecider {

	private final EditorContext editor;

	public SaveDecider(final MPart editorPart) {
		this.editor = new EditorContext(editorPart);
	}

	void save() {
		if (this.canSaveFile()) this.tryToSave();
	}

	private void tryToSave() {
		try {
			this.editor.save();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private boolean canSaveFile() {
		if (this.bufferIsNotModified()) return false;
		if (SaveDecider.hasWarnings()) return false;
		if (SaveDecider.hasErrors()) return false;
		return true;
	}

	private boolean bufferIsNotModified() {
		return !this.editor.isModified();
	}

	private static boolean hasWarnings() {
		return EditorContext.canCheckWarnings() && EditorContext.hasWarnings();
	}

	private static boolean hasErrors() {
		return EditorContext.canCheckErrors() && EditorContext.hasErrors();
	}
}
