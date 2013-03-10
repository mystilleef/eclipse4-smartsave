package com.laboki.eclipse.plugin.smartsave.saver.preferences.ui;

import lombok.ToString;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.plugin.smartsave.saver.preferences.PreferenceStore;

@ToString
final class WarningResponseComboViewer extends PreferencesResponseComboViewer {

	public WarningResponseComboViewer(final Composite parent) {
		super(parent);
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {
		PreferenceStore.setCanSaveIfWarnings(this.getSelectionValue(event));
	}

	@Override
	protected void updateSelection() {
		if (PreferenceStore.getCanSaveIfWarnings()) this.setSelection(PreferencesResponseComboViewer.YES);
		else this.setSelection(PreferencesResponseComboViewer.NO);
	}
}
