package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.plugin.smartsave.preferences.PreferenceStore;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;

final class ErrorResponseComboViewer extends PreferencesResponseComboViewer {

	public ErrorResponseComboViewer(final Composite parent, final EventBus eventBus) {
		super(parent, eventBus);
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
}
