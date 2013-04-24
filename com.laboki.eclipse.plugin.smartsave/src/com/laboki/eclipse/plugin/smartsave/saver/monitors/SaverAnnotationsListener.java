package com.laboki.eclipse.plugin.smartsave.saver.monitors;

import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;

public class SaverAnnotationsListener extends AbstractSaverListener implements IAnnotationModelListener {

	private final IAnnotationModel annotationModel = EditorContext.getView(EditorContext.getEditor()).getAnnotationModel();

	public SaverAnnotationsListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Override
	public void add() {
		this.annotationModel.addAnnotationModelListener(this);
	}

	@Override
	public void remove() {
		this.annotationModel.addAnnotationModelListener(this);
	}

	@Override
	public void modelChanged(final IAnnotationModel model) {
		this.scheduleSave();
	}
}
