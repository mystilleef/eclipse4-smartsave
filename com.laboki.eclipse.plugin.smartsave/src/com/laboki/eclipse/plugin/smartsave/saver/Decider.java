package com.laboki.eclipse.plugin.smartsave.saver;

import lombok.ToString;

import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.smartsave.saver.listeners.ISaverCompletionListener;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverCompletionListener;

@ToString
final class Decider {

	private final IEditorPart editor = EditorContext.getEditor();
	private final ContentAssistant contentAssistant = new ContentAssistant();

	public void save() {
		if (!EditorContext.canSaveAutomatically()) return;
		EditorContext.syncFile(this.editor);
		if (!this.canSaveFile()) return;
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
		if (this.bufferContentAssistantIsVisible()) return true;
		if (this.bufferIsInLinkMode()) return true;
		return false;
	}

	private boolean bufferHasSelection() {
		if (EditorContext.hasSelection(this.editor)) return true;
		if (EditorContext.hasBlockSelection(this.editor)) return true;
		return false;
	}

	private boolean bufferContentAssistantIsVisible() {
		return this.contentAssistant.isVisible();
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

	private final class ContentAssistant implements ISaverCompletionListener {

		private boolean isVisible;
		private final SaverCompletionListener listener = new SaverCompletionListener(this);

		public ContentAssistant() {
			this.listener.start();
		}

		@Override
		public void assistSessionStarted() {
			this.isVisible = true;
		}

		@Override
		public void assistSessionEnded() {
			this.isVisible = false;
		}

		public boolean isVisible() {
			return this.isVisible;
		}
	}
}
