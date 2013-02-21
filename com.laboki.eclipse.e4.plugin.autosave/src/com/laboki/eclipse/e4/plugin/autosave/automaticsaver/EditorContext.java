package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

final class EditorContext {

	private final MPart editorPart;

	public EditorContext(final MPart editorPart) {
		this.editorPart = editorPart;
	}

	void save() throws IllegalAccessException {
		this.getPartService().savePart(this.editorPart, false);
	}

	private EPartService getPartService() throws IllegalAccessException {
		final IEclipseContext context = this.editorPart.getContext();
		if (context == null) throw new IllegalAccessException();
		final EPartService partService = context.get(EPartService.class);
		if (partService == null) throw new IllegalAccessException();
		return partService;
	}

	boolean isModified() {
		return this.editorPart.isDirty();
	}

	static boolean hasWarnings() {
		return false;
	}

	static boolean hasErrors() {
		return false;
	}

	static boolean canCheckWarnings() {
		return false;
	}

	static boolean canCheckErrors() {
		return false;
	}
}
