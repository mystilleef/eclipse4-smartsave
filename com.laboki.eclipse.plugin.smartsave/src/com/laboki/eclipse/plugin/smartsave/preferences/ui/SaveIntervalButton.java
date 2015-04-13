package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

final class SaveIntervalButton extends AbstractEventBusInstance {

	private static final String TASK_NAME = "save interval button task";
	private static final TaskMutexRule RULE = new TaskMutexRule();
	private static final int ZERO = 0;
	private final Button button;
	private SaveIntervalDialog dialog;
	private static final int SIXTY_SECONDS = 60;
	private final SelectionListener buttonListener = new ButtonListener();
	private final Composite composite;

	public SaveIntervalButton(final Composite composite) {
		super();
		this.composite = composite;
		this.button = new Button(composite, SWT.FLAT);
	}

	void updateText() {
		if ((this.button == null) || this.button.isDisposed()) return;
		this.button.setText(SaveIntervalButton
			.minutesAndSeconds(Store.getSaveIntervalInSeconds()));
		this.button.pack();
		this.button.update();
	}

	private static String minutesAndSeconds(final int intervalInSeconds) {
		final int minutes = SaveIntervalButton.getMinutes(intervalInSeconds);
		final int seconds = SaveIntervalButton.getSeconds(intervalInSeconds);
		return SaveIntervalButton.formatMinutesAndSeconds(minutes, seconds);
	}

	private static int getMinutes(final int intervalInSeconds) {
		return intervalInSeconds / SaveIntervalButton.SIXTY_SECONDS;
	}

	private static int getSeconds(final int intervalInSeconds) {
		return intervalInSeconds % SaveIntervalButton.SIXTY_SECONDS;
	}

	private static String formatMinutesAndSeconds(final int minutes,
		final int seconds) {
		if (SaveIntervalButton.zero(minutes)) return MessageFormat.format(
			" {0} sec ", String.valueOf(seconds));
		if (SaveIntervalButton.zero(seconds)) return MessageFormat.format(
			" {0} min ", String.valueOf(minutes));
		return MessageFormat.format(" {0} min {1} sec ",
			String.valueOf(minutes),
			String.valueOf(seconds));
	}

	private static boolean zero(final int minutes) {
		return minutes == SaveIntervalButton.ZERO;
	}

	@Override
	public Instance start() {
		this.startListening();
		this.updateText();
		return super.start();
	}

	public void startListening() {
		this.button.addSelectionListener(this.buttonListener);
	}

	@Subscribe
	public void preferencesChanged(final PreferenceStoreChangeEvent event) {
		new AsyncTask() {

			@Override
			public void execute() {
				SaveIntervalButton.this.updateText();
			}
		}.setName(SaveIntervalButton.TASK_NAME)
			.setRule(SaveIntervalButton.RULE)
			.start();
	}

	public void showSaveIntervalDialog() {
		if (this.dialog == null) this.showNewDialog();
		else this.dialog.show();
	}

	private void showNewDialog() {
		this.dialog = new SaveIntervalDialog(this.composite);
		this.dialog.start();
		this.dialog.show();
	}

	private final class ButtonListener implements SelectionListener, Runnable {

		public ButtonListener() {}

		@Override
		public void run() {
			SaveIntervalButton.this.showSaveIntervalDialog();
		}

		@Override
		public void widgetDefaultSelected(final SelectionEvent event) {
			this.widgetSelected(event);
		}

		@Override
		public void widgetSelected(final SelectionEvent event) {
			EditorContext.asyncExec(this);
		}
	}
}
