package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;

import com.laboki.eclipse.plugin.smartsave.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;

public class AnnotationsListener extends AbstractListener implements IAnnotationModelListener {

	private final IAnnotationModel annotationModel = AnnotationsListener.getAnnotationModel();

	public AnnotationsListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public void add() {
		if (this.annotationModel == null) return;
		this.annotationModel.addAnnotationModelListener(this);
	}

	@Override
	public void remove() {
		if (this.annotationModel == null) return;
		this.annotationModel.removeAnnotationModelListener(this);
	}

	@Override
	public void modelChanged(final IAnnotationModel model) {
		this.scheduleSave();
	}

	private static IAnnotationModel getAnnotationModel() {
		try {
			return EditorContext.getView(EditorContext.getEditor()).getAnnotationModel();
		} catch (final Exception e) {
			return null;
		}
	}
}
