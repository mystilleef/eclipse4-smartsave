package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

final class SaveIntervalDialog {

	private static Shell dialog;

	public SaveIntervalDialog() {}

	public SaveIntervalDialog(final Composite composite) {
		SaveIntervalDialog.setDialog(new Shell(composite.getShell(), SWT.DIALOG_TRIM));
	}

	public static void show() {
		SaveIntervalDialog.getDialog().setVisible(true);
	}

	public static Shell getDialog() {
		return SaveIntervalDialog.dialog;
	}

	public static void setDialog(final Shell dialog) {
		SaveIntervalDialog.dialog = dialog;
	}
}
