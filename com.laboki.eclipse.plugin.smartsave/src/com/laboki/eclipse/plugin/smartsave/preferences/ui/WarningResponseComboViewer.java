package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;

final class WarningResponseComboViewer extends PreferencesResponseComboViewer {

	public WarningResponseComboViewer(final Composite parent, final EventBus eventBus) {
		super(parent, eventBus);
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {
		Store.setCanSaveIfWarnings(this.getSelectionValue(event));
	}

	@Override
	protected void updateSelection() {
		if (Store.getCanSaveIfWarnings()) this.setSelection(PreferencesResponseComboViewer.YES);
		else this.setSelection(PreferencesResponseComboViewer.NO);
	}
}
