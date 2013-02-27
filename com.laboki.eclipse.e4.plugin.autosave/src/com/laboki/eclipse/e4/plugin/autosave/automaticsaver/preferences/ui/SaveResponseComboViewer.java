package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.ui;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.PreferencesStore;

final class SaveResponseComboViewer extends PreferencesResponseComboViewer {

	public SaveResponseComboViewer(final Composite parent) {
		super(parent);
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {
		PreferencesStore.setCanSaveAutomatically(this.getSelectionValue(event));
	}

	@Override
	protected void updateSelection() {
		if (PreferencesStore.getCanSaveAutomatically()) this.setSelection(0);
		else this.setSelection(1);
	}
}
