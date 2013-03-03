package com.laboki.eclipse.plugin.smartsave.saver.preferences.ui;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.plugin.smartsave.saver.preferences.PreferenceStore;

final class ErrorResponseComboViewer extends PreferencesResponseComboViewer {

	public ErrorResponseComboViewer(final Composite parent) {
		super(parent);
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {
		PreferenceStore.setCanSaveIfErrors(this.getSelectionValue(event));
	}

	@Override
	protected void updateSelection() {
		if (PreferenceStore.getCanSaveIfErrors()) this.setSelection(PreferencesResponseComboViewer.YES);
		else this.setSelection(PreferencesResponseComboViewer.NO);
	}

	@Override
	public String toString() {
		return String.format("ErrorResponseComboViewer [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
