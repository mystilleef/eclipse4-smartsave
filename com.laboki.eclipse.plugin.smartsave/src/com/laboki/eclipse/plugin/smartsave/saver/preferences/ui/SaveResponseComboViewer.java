package com.laboki.eclipse.plugin.smartsave.saver.preferences.ui;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.plugin.smartsave.saver.preferences.PreferenceStore;

final class SaveResponseComboViewer extends PreferencesResponseComboViewer {

	public SaveResponseComboViewer(final Composite parent) {
		super(parent);
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {
		PreferenceStore.setCanSaveAutomatically(this.getSelectionValue(event));
	}

	@Override
	protected void updateSelection() {
		if (PreferenceStore.getCanSaveAutomatically()) this.setSelection(0);
		else this.setSelection(1);
	}

	@Override
	public String toString() {
		return String.format("SaveResponseComboViewer [toString()=%s, getClass()=%s]", super.toString(), this.getClass());
	}
}
