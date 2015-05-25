package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;

public class AnnotationsListener extends BaseListener
	implements
		IAnnotationModelListener {

	private final Optional<IAnnotationModel> model =
		AnnotationsListener.getAnnotationModel();

	@Override
	public void
	add() {
		if (!this.model.isPresent()) return;
		this.model.get().addAnnotationModelListener(this);
	}

	@Override
	public void
	remove() {
		if (!this.model.isPresent()) return;
		this.model.get().removeAnnotationModelListener(this);
	}

	@Override
	public void
	modelChanged(final IAnnotationModel model) {
		BaseListener.scheduleSave();
	}

	private static Optional<IAnnotationModel>
	getAnnotationModel() {
		final Optional<IEditorPart> editor = EditorContext.getEditor();
		if (!editor.isPresent()) return Optional.absent();
		final Optional<SourceViewer> view = EditorContext.getView(editor);
		if (!view.isPresent()) return Optional.absent();
		return Optional.fromNullable(view.get().getAnnotationModel());
	}
}
