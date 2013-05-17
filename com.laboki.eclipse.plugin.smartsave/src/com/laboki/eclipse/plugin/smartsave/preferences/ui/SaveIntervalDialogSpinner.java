package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.FocusSaveIntervalDialogSpinnerEvent;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

final class SaveIntervalDialogSpinner extends AbstractEventBusInstance {

	private static final int TEXT_LIMIT = 3;
	private static final int SPINNER_PAGE_INCREMENTS = 10;
	private static final int SPINNER_INCREMENTS = 1;
	private static final int SPINNER_DIGITS = 0;
	private static final int SPINNER_MAXIMUM = 600;
	private static final int SPINNER_MINIMUM = 1;
	private final ModifyListener modifyListener = new SpinnerModifyListener();
	private final SpinnerTraverseListener traverseListener = new SpinnerTraverseListener();
	private final Spinner spinner;

	public SaveIntervalDialogSpinner(final Composite composite, final EventBus eventBus) {
		super(eventBus);
		this.spinner = new Spinner(composite, SWT.BORDER | SWT.RIGHT);
	}

	private void updateProperties() {
		this.spinner.setTextLimit(SaveIntervalDialogSpinner.TEXT_LIMIT);
		this.spinner.setValues(Store.getSaveIntervalInSeconds(), SaveIntervalDialogSpinner.SPINNER_MINIMUM, SaveIntervalDialogSpinner.SPINNER_MAXIMUM, SaveIntervalDialogSpinner.SPINNER_DIGITS, SaveIntervalDialogSpinner.SPINNER_INCREMENTS, SaveIntervalDialogSpinner.SPINNER_PAGE_INCREMENTS);
		this.focus();
	}

	public void startListening() {
		this.spinner.addModifyListener(this.modifyListener);
		this.spinner.addTraverseListener(this.traverseListener);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void preferencesChanged(@SuppressWarnings("unused") final PreferenceStoreChangeEvent event) {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				SaveIntervalDialogSpinner.this.updateSelection();
			}
		}.begin();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void focusSpinner(@SuppressWarnings("unused") final FocusSaveIntervalDialogSpinnerEvent event) {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				SaveIntervalDialogSpinner.this.focus();
			}
		}.begin();
	}

	private void focus() {
		this.spinner.setFocus();
		this.spinner.forceFocus();
	}

	private synchronized void updateSelection() {
		if (this.spinner.getSelection() == Store.getSaveIntervalInSeconds()) return;
		this.spinner.removeModifyListener(this.modifyListener);
		this.spinner.setSelection(Store.getSaveIntervalInSeconds());
		this.spinner.addModifyListener(this.modifyListener);
	}

	public Spinner getSpinner() {
		return this.spinner;
	}

	@Override
	public Instance begin() {
		this.startListening();
		this.updateProperties();
		return super.begin();
	}

	@Override
	public Instance end() {
		return super.end();
	}

	private final class SpinnerModifyListener implements ModifyListener, Runnable {

		public SpinnerModifyListener() {}

		@Override
		public void run() {
			Store.setSaveIntervalInSeconds(SaveIntervalDialogSpinner.this.getSpinner().getSelection());
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
			if (event.detail == SWT.TRAVERSE_RETURN) SaveIntervalDialogSpinner.this.getSpinner().getShell().close();
		}
	}
}
