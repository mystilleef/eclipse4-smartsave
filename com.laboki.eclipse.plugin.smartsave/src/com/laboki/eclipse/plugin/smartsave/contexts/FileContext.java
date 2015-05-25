package com.laboki.eclipse.plugin.smartsave.contexts;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.google.common.base.Optional;

public enum FileContext {
	INSTANCE;

	public static Optional<IFile>
	getFile() {
		final Optional<IEditorPart> editor = EditorContext.getEditor();
		final Optional<FileEditorInput> fileEditorInput =
			FileContext.getFileEditorInput(editor);
		if (!fileEditorInput.isPresent()) return Optional.absent();
		return Optional.fromNullable(fileEditorInput.get().getFile());
	}

	public static Optional<IFile>
	getFile(final Optional<IEditorPart> editor) {
		final Optional<FileEditorInput> fileEditorInput =
			FileContext.getFileEditorInput(editor);
		if (!fileEditorInput.isPresent()) return Optional.absent();
		return Optional.fromNullable(fileEditorInput.get().getFile());
	}

	private static Optional<FileEditorInput>
	getFileEditorInput(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		final IEditorInput editorInput = editor.get().getEditorInput();
		if (!(editorInput instanceof IFileEditorInput)) return Optional.absent();
		return Optional.fromNullable((FileEditorInput) editorInput);
	}

	public static IContentType[]
	getContentTypes() {
		return Platform.getContentTypeManager().getAllContentTypes();
	}
}
