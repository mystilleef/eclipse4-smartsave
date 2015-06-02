package com.laboki.eclipse.plugin.smartsave.contexts;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.common.base.Optional;

public enum DocumentContext {
	INSTANCE;

	public static Optional<IDocument>
	getDocument(final Optional<IEditorPart> editor) {
		final Optional<IDocumentProvider> provider =
			DocumentContext.getDocumentProvider(editor);
		if (!provider.isPresent()) return Optional.absent();
		return Optional.fromNullable(provider.get().getDocument(editor.get()
			.getEditorInput()));
	}

	private static Optional<IDocumentProvider>
	getDocumentProvider(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		final IEditorPart part = editor.get();
		if (DocumentContext.isNotTextEditor(part)) return Optional.absent();
		return Optional.fromNullable(((ITextEditor) part).getDocumentProvider());
	}

	private static boolean
	isNotTextEditor(final IEditorPart part) {
		return !DocumentContext.isTextEditor(part);
	}

	private static boolean
	isTextEditor(final IEditorPart part) {
		return part instanceof ITextEditor;
	}
}
