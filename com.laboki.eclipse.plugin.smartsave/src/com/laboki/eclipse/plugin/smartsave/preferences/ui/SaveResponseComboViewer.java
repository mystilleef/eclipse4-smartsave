package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.plugin.smartsave.preferences.PreferenceStore;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;

final class SaveResponseComboViewer extends PreferencesResponseComboViewer {

	public SaveResponseComboViewer(final Composite parent, final EventBus eventBus) {
		super(parent, eventBus);
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
}
