package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.plugin.smartsave.preferences.Store;

final class ErrorResponseComboViewer extends PreferencesResponseComboViewer {

  public ErrorResponseComboViewer(final Composite parent) {
    super(parent);
  }

  @Override
  protected void handleResponseSelection(final SelectionChangedEvent event) {
    Store.setCanSaveIfErrors(this.getSelectionValue(event));
  }

  @Override
  protected void updateSelection() {
    if (Store.getCanSaveIfErrors()) this
        .setSelection(PreferencesResponseComboViewer.YES);
    else this.setSelection(PreferencesResponseComboViewer.NO);
  }
}
