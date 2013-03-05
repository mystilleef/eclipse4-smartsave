package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

final class Decider {

	private final IEditorPart editor = EditorContext.getEditor();
	private final ContentAssistant contentAssistant = new ContentAssistant();

	public void save() {
		if (EditorContext.canSaveAutomatically() && this.canSaveFile()) EditorContext.save(this.editor);
	}

	private boolean canSaveFile() {
		if (this.bufferIsNotModified()) return false;
		if (this.bufferIsInEditingMode()) return false;
		if (this.bufferHoversHaveFocus()) return false;
		if (this.bufferHasProblems()) return false;
		return true;
	}

	private boolean bufferHoversHaveFocus() {
		return EditorContext.hoversHaveFocus(this.editor);
	}

	private boolean bufferIsNotModified() {
		return !EditorContext.isModified(this.editor);
	}

	private boolean bufferIsInEditingMode() {
		if (this.bufferHasSelection()) return true;
		if (this.bufferIsInLinkMode()) return true;
		if (this.bufferContentAssistantIsVisible()) return true;
		return false;
	}

	private boolean bufferContentAssistantIsVisible() {
		return this.contentAssistant.isVisible();
	}

	private boolean bufferHasSelection() {
		if (EditorContext.hasSelection(this.editor)) return true;
		if (EditorContext.hasBlockSelection(this.editor)) return true;
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
		if (EditorContext.canSaveIfErrors()) return false;
		return EditorContext.hasErrors(this.editor);
	}

	private boolean bufferHasWarnings() {
		if (EditorContext.canSaveIfWarnings()) return false;
		return EditorContext.hasWarnings(this.editor);
	}

	@Override
	public String toString() {
		return String.format("Decider [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
