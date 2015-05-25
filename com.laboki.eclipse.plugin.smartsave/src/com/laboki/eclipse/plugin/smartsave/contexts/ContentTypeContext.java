package com.laboki.eclipse.plugin.smartsave.contexts;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;

public enum ContentTypeContext {
	INSTANCE;

	private static final Logger LOGGER =
		Logger.getLogger(ContentTypeContext.class.getName());

	public static Optional<IContentType>
	getContentType() {
		final Optional<IEditorPart> editor = EditorContext.getEditor();
		if (!editor.isPresent()) return Optional.absent();
		final Optional<IContentDescription> description =
			ContentTypeContext.getContentDescription(editor);
		if (!description.isPresent()) return Optional.absent();
		return Optional.fromNullable(description.get().getContentType());
	}

	public static String
	getContentTypeId(final Optional<IEditorPart> editor) {
		final Optional<IContentType> contentType =
			ContentTypeContext.getContentType(editor);
		if (contentType.isPresent()) return contentType.get().getId();
		return "";
	}

	public static Optional<IContentType>
	getContentType(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		final Optional<IContentDescription> description =
			ContentTypeContext.getContentDescription(editor);
		if (!description.isPresent()) return Optional.absent();
		return Optional.fromNullable(description.get().getContentType());
	}

	private static Optional<IContentDescription>
	getContentDescription(final Optional<IEditorPart> editor) {
		try {
			return ContentTypeContext.tryToGetContentDescription(editor);
		}
		catch (final CoreException e) {
			ContentTypeContext.LOGGER.log(Level.WARNING, e.getMessage(), e);
			return Optional.absent();
		}
	}

	private static Optional<IContentDescription>
	tryToGetContentDescription(final Optional<IEditorPart> editor)
		throws CoreException {
		final Optional<IFile> file = EditorContext.getFile(editor);
		if (!file.isPresent()) return Optional.absent();
		return Optional.fromNullable(file.get().getContentDescription());
	}

	public static Optional<IContentType>
	getContentTypeFromFile(final Optional<IFile> file) {
		final Optional<IContentDescription> description =
			ContentTypeContext.getContentDescriptionFromFile(file);
		if (!description.isPresent()) return Optional.absent();
		return Optional.fromNullable(description.get().getContentType());
	}

	private static Optional<IContentDescription>
	getContentDescriptionFromFile(final Optional<IFile> file) {
		try {
			return ContentTypeContext.tryToGetContentDescriptionFromFile(file);
		}
		catch (final Exception e) {
			ContentTypeContext.LOGGER.log(Level.WARNING, e.getMessage(), e);
			return Optional.absent();
		}
	}

	private static Optional<IContentDescription>
	tryToGetContentDescriptionFromFile(final Optional<IFile> file)
		throws CoreException {
		if (!file.isPresent()) return Optional.absent();
		return Optional.fromNullable(file.get().getContentDescription());
	}

	public static IContentType[]
	getContentTypes() {
		return Platform.getContentTypeManager().getAllContentTypes();
	}
}
