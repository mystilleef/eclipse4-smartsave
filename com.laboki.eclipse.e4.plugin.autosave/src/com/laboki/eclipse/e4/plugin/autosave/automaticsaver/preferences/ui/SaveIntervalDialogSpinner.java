package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.ActivePart;
import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.IPreferencesHandler;
import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.PreferencesListener;
import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.PreferencesStore;

final class SaveIntervalDialogSpinner implements IPreferencesHandler {

	private static final int TEXT_LIMIT = 3;
	private static final int SPINNER_PAGE_INCREMENTS = 10;
	private static final int SPINNER_INCREMENTS = 1;
	private static final int SPINNER_DIGITS = 0;
	private static final int SPINNER_MAXIMUM = 600;
	private static final int SPINNER_MINIMUM = 1;
	private final PreferencesListener preferencesListener = new PreferencesListener(this);
	private final ModifyListener modifyListener = new SpinnerModifyListener();
	private final SpinnerTraverseListener traverseListener = new SpinnerTraverseListener();
	private static Spinner spinner;

	public SaveIntervalDialogSpinner(final Composite composite) {
		SaveIntervalDialogSpinner.spinner = new Spinner(composite, SWT.BORDER | SWT.RIGHT);
		SaveIntervalDialogSpinner.setProperties();
	}

	private static void setProperties() {
		SaveIntervalDialogSpinner.getSpinner().setTextLimit(SaveIntervalDialogSpinner.TEXT_LIMIT);
		SaveIntervalDialogSpinner.getSpinner().setValues(PreferencesStore.getSaveIntervalInSeconds(), SaveIntervalDialogSpinner.SPINNER_MINIMUM, SaveIntervalDialogSpinner.SPINNER_MAXIMUM, SaveIntervalDialogSpinner.SPINNER_DIGITS, SaveIntervalDialogSpinner.SPINNER_INCREMENTS, SaveIntervalDialogSpinner.SPINNER_PAGE_INCREMENTS);
		SaveIntervalDialogSpinner.getSpinner().setFocus();
	}

	public void startListening() {
		this.preferencesListener.start();
		SaveIntervalDialogSpinner.spinner.addModifyListener(this.modifyListener);
		SaveIntervalDialogSpinner.spinner.addTraverseListener(this.traverseListener);
	}

	@Override
	public void preferencesChanged() {
		this.updateSelection();
	}

	private void updateSelection() {
		if (SaveIntervalDialogSpinner.spinner.getSelection() == PreferencesStore.getSaveIntervalInSeconds()) return;
		SaveIntervalDialogSpinner.spinner.removeModifyListener(this.modifyListener);
		SaveIntervalDialogSpinner.spinner.setSelection(PreferencesStore.getSaveIntervalInSeconds());
		SaveIntervalDialogSpinner.spinner.addModifyListener(this.modifyListener);
	}

	public static Spinner getSpinner() {
		return SaveIntervalDialogSpinner.spinner;
	}

	private final class SpinnerModifyListener implements ModifyListener, Runnable {

		public SpinnerModifyListener() {}

		@Override
		public void run() {
			PreferencesStore.setSaveIntervalInSeconds(SaveIntervalDialogSpinner.getSpinner().getSelection());
		}

		@Override
		public void modifyText(final ModifyEvent event) {
			ActivePart.asyncExec(this);
		}
	}

	private final class SpinnerTraverseListener implements TraverseListener {

		public SpinnerTraverseListener() {}

		@Override
		public void keyTraversed(final TraverseEvent event) {
			if (event.detail == SWT.TRAVERSE_RETURN) SaveIntervalDialogSpinner.getSpinner().getShell().close();
		}
	}

	@Override
	public String toString() {
		return String.format("SaveIntervalDialogSpinner [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
