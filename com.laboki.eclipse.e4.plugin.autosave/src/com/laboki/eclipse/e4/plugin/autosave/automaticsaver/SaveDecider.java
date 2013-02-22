package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

final class SaveDecider {

	private SaveDecider() {}

	static void save() {
		if (SaveDecider.canSaveFile()) ActivePart.save();
	}

	private static boolean canSaveFile() {
		if (SaveDecider.bufferIsNotModified()) return false;
		if (SaveDecider.hasWarnings()) return false;
		if (SaveDecider.hasErrors()) return false;
		return true;
	}

	private static boolean bufferIsNotModified() {
		return !ActivePart.isModified();
	}

	private static boolean hasWarnings() {
		return ActivePart.canCheckWarnings() && ActivePart.hasWarnings();
	}

	private static boolean hasErrors() {
		return ActivePart.canCheckErrors() && ActivePart.hasErrors();
	}
}
