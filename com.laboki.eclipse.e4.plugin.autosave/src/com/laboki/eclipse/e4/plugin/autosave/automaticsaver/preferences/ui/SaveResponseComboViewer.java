package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.IPreferencesHandler;
import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.PreferencesListener;
import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.PreferencesStore;

final class SaveResponseComboViewer extends ResponseComboViewer implements IPreferencesHandler {

	private final PreferencesListener listener = new PreferencesListener(this);

	public SaveResponseComboViewer(final Composite parent) {
		super(parent);
		this.setComboProperties();
		this.listener.start();
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {
		super.handleResponseSelection(event);
		final boolean saveAutomatically = ((Response) ((IStructuredSelection) event.getSelection()).getFirstElement()).value();
		PreferencesStore.setCanSaveAutomatically(saveAutomatically);
	}

	private void setComboProperties() {
		this.updateSelection();
	}

	private void updateSelection() {
		final boolean saveAutomatically = PreferencesStore.getCanSaveAutomatically();
		if (saveAutomatically) this.setSelection(0);
		else this.setSelection(1);
	}

	private void setSelection(final int index) {
		this.stopListening();
		this.setSelection(new StructuredSelection(this.getResponses()[index]));
		this.startListening();
	}

	@Override
	public void preferencesChanged() {
		this.updateSelection();
	}
}
