package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public class AnnotationsListener extends AbstractListener
	implements
		IAnnotationModelListener {

	private final Optional<IAnnotationModel> annotationModel =
		AnnotationsListener.getAnnotationModel();

	public AnnotationsListener() {
		super();
	}

	@Override
	public void
	add() {
		if (!this.annotationModel.isPresent()) return;
		this.annotationModel.get().addAnnotationModelListener(this);
	}

	@Override
	public void
	remove() {
		if (!this.annotationModel.isPresent()) return;
		this.annotationModel.get().removeAnnotationModelListener(this);
	}

	@Override
	public void
	modelChanged(final IAnnotationModel model) {
		AbstractListener.scheduleSave();
	}

	private static Optional<IAnnotationModel>
	getAnnotationModel() {
		final Optional<IEditorPart> editor = EditorContext.getEditor();
		if (!editor.isPresent()) return Optional.absent();
		final Optional<SourceViewer> view = EditorContext.getView(editor.get());
		if (!view.isPresent()) return Optional.absent();
		return Optional.fromNullable(view.get().getAnnotationModel());
	}
}
