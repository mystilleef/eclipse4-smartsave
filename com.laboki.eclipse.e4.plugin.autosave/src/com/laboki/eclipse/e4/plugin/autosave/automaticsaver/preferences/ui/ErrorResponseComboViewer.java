package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.ui;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.PreferencesStore;

final class ErrorResponseComboViewer extends PreferencesResponseComboViewer {

	public ErrorResponseComboViewer(final Composite parent) {
		super(parent);
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {
		PreferencesStore.setCanSaveIfErrors(this.getSelectionValue(event));
	}

	@Override
	protected void updateSelection() {
		if (PreferencesStore.getCanSaveIfErrors()) this.setSelection(PreferencesResponseComboViewer.YES);
		else this.setSelection(PreferencesResponseComboViewer.NO);
	}
}
