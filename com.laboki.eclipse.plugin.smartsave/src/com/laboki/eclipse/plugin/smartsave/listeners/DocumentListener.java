package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;

public final class DocumentListener extends BaseListener
	implements
		IDocumentListener {

	private final Optional<IDocument> document =
		EditorContext.getDocument(EditorContext.getEditor());

	@Override
	public void
	documentAboutToBeChanged(final DocumentEvent event) {
		EditorContext.cancelSaverTasks();
	}

	@Override
	public void
	documentChanged(final DocumentEvent event) {
		BaseListener.scheduleSave();
	}

	@Override
	protected void
	add() {
		if (!this.document.isPresent()) return;
		this.document.get().addDocumentListener(this);
	}

	@Override
	protected void
	remove() {
		if (!this.document.isPresent()) return;
		this.document.get().removeDocumentListener(this);
	}
}
