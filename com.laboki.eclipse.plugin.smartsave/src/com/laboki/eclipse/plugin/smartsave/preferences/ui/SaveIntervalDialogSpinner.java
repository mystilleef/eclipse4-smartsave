package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import com.laboki.eclipse.plugin.smartsave.preferences.IPreferenceHandler;
import com.laboki.eclipse.plugin.smartsave.preferences.PreferenceListener;
import com.laboki.eclipse.plugin.smartsave.preferences.PreferenceStore;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

final class SaveIntervalDialogSpinner implements IPreferenceHandler {

	private static final int TEXT_LIMIT = 3;
	private static final int SPINNER_PAGE_INCREMENTS = 10;
	private static final int SPINNER_INCREMENTS = 1;
	private static final int SPINNER_DIGITS = 0;
	private static final int SPINNER_MAXIMUM = 600;
	private static final int SPINNER_MINIMUM = 1;
	private final PreferenceListener preferenceListener = new PreferenceListener(this);
	private final ModifyListener modifyListener = new SpinnerModifyListener();
	private final SpinnerTraverseListener traverseListener = new SpinnerTraverseListener();
	private static Spinner spinner;

	public SaveIntervalDialogSpinner(final Composite composite) {
		SaveIntervalDialogSpinner.spinner = new Spinner(composite, SWT.BORDER | SWT.RIGHT);
		SaveIntervalDialogSpinner.updateProperties();
	}

	private static void updateProperties() {
		SaveIntervalDialogSpinner.spinner.setTextLimit(SaveIntervalDialogSpinner.TEXT_LIMIT);
		SaveIntervalDialogSpinner.spinner.setValues(PreferenceStore.getSaveIntervalInSeconds(), SaveIntervalDialogSpinner.SPINNER_MINIMUM, SaveIntervalDialogSpinner.SPINNER_MAXIMUM, SaveIntervalDialogSpinner.SPINNER_DIGITS, SaveIntervalDialogSpinner.SPINNER_INCREMENTS, SaveIntervalDialogSpinner.SPINNER_PAGE_INCREMENTS);
		SaveIntervalDialogSpinner.spinner.setFocus();
	}

	public void startListening() {
		this.preferenceListener.start();
		SaveIntervalDialogSpinner.spinner.addModifyListener(this.modifyListener);
		SaveIntervalDialogSpinner.spinner.addTraverseListener(this.traverseListener);
	}

	@Override
	public void preferencesChanged() {
		this.updateSelection();
	}

	private void updateSelection() {
		if (SaveIntervalDialogSpinner.spinner.getSelection() == PreferenceStore.getSaveIntervalInSeconds()) return;
		SaveIntervalDialogSpinner.spinner.removeModifyListener(this.modifyListener);
		SaveIntervalDialogSpinner.spinner.setSelection(PreferenceStore.getSaveIntervalInSeconds());
		SaveIntervalDialogSpinner.spinner.addModifyListener(this.modifyListener);
	}

	public static Spinner getSpinner() {
		return SaveIntervalDialogSpinner.spinner;
	}

	private final class SpinnerModifyListener implements ModifyListener, Runnable {

		public SpinnerModifyListener() {}

		@Override
		public void run() {
			PreferenceStore.setSaveIntervalInSeconds(SaveIntervalDialogSpinner.getSpinner().getSelection());
		}

		@Override
		public void modifyText(final ModifyEvent event) {
			EditorContext.asyncExec(this);
		}
	}

	private final class SpinnerTraverseListener implements TraverseListener {

		public SpinnerTraverseListener() {}

		@Override
		public void keyTraversed(final TraverseEvent event) {
			if (event.detail == SWT.TRAVERSE_RETURN) SaveIntervalDialogSpinner.getSpinner().getShell().close();
		}
	}
}
