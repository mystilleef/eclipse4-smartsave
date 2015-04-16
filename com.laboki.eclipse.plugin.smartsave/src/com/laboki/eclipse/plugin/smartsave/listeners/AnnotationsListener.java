package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;

import com.laboki.eclipse.plugin.smartsave.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public class AnnotationsListener extends AbstractListener
	implements
		IAnnotationModelListener {

	private final IAnnotationModel annotationModel =
		AnnotationsListener.getAnnotationModel();

	public AnnotationsListener() {
		super();
	}

	@Override
	public void
	add() {
		if (this.annotationModel == null) return;
		this.annotationModel.addAnnotationModelListener(this);
	}

	@Override
	public void
	remove() {
		if (this.annotationModel == null) return;
		this.annotationModel.removeAnnotationModelListener(this);
	}

	@Override
	public void
	modelChanged(final IAnnotationModel model) {
		AbstractListener.scheduleSave();
	}

	private static IAnnotationModel
	getAnnotationModel() {
		try {
			return EditorContext.getView(EditorContext.getEditor())
				.getAnnotationModel();
		}
		catch (final Exception e) {
			return null;
		}
	}
}
