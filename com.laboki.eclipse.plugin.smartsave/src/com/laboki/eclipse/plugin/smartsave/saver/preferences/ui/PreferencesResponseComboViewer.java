package com.laboki.eclipse.plugin.smartsave.saver.preferences.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.plugin.smartsave.saver.preferences.IPreferenceHandler;
import com.laboki.eclipse.plugin.smartsave.saver.preferences.PreferenceListener;

class PreferencesResponseComboViewer extends ResponseComboViewer implements IPreferenceHandler {

	private final PreferenceListener listener = new PreferenceListener(this);
	public static final int YES = 0;
	public static final int NO = 1;

	public PreferencesResponseComboViewer(final Composite parent) {
		super(parent);
		this.updateComboProperties();
		this.listener.start();
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {}

	protected boolean getSelectionValue(final SelectionChangedEvent event) {
		super.handleResponseSelection(event);
		return ((Response) ((IStructuredSelection) event.getSelection()).getFirstElement()).value();
	}

	private void updateComboProperties() {
		this.updateSelection();
	}

	protected void updateSelection() {}

	protected void setSelection(final int index) {
		this.stopListening();
		this.setSelection(new StructuredSelection(this.getResponses()[index]));
		this.startListening();
	}

	@Override
	public void preferencesChanged() {
		this.updateSelection();
	}

	@Override
	public String toString() {
		return String.format("PreferencesResponseComboViewer [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
