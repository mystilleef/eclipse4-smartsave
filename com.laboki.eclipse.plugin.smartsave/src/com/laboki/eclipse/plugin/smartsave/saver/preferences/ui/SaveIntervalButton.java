package com.laboki.eclipse.plugin.smartsave.saver.preferences.ui;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.preferences.IPreferenceHandler;
import com.laboki.eclipse.plugin.smartsave.saver.preferences.PreferenceListener;
import com.laboki.eclipse.plugin.smartsave.saver.preferences.PreferenceStore;

final class SaveIntervalButton implements IPreferenceHandler {

	private static Button button;
	private static SaveIntervalDialog dialog;
	private static final int SIXTY_SECONDS = 60;
	private final PreferenceListener preferenceListener = new PreferenceListener(this);
	private final SelectionListener buttonListener = new ButtonListener();
	private final Composite composite;

	public SaveIntervalButton(final Composite composite) {
		this.composite = composite;
		SaveIntervalButton.button = new Button(composite, SWT.FLAT);
		SaveIntervalButton.updateText();
	}

	private static void updateText() {
		SaveIntervalButton.button.setText(SaveIntervalButton.minutesAndSeconds(PreferenceStore.getSaveIntervalInSeconds()));
		SaveIntervalButton.button.pack();
		SaveIntervalButton.button.update();
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

	private static String formatMinutesAndSeconds(final int minutes, final int seconds) {
		if (minutes == 0) return MessageFormat.format(" {0} sec ", String.valueOf(seconds));
		if (seconds == 0) return MessageFormat.format(" {0} min ", String.valueOf(minutes));
		return MessageFormat.format(" {0} min {1} sec ", String.valueOf(minutes), String.valueOf(seconds));
	}

	public void startListening() {
		this.preferenceListener.start();
		SaveIntervalButton.button.addSelectionListener(this.buttonListener);
	}

	@Override
	public void preferencesChanged() {
		SaveIntervalButton.updateText();
	}

	@SuppressWarnings("unused")
	public void showSaveIntervalDialog() {
		if (SaveIntervalButton.dialog == null) {
			new SaveIntervalDialog(this.composite);
			SaveIntervalDialog.show();
		} else SaveIntervalDialog.show();
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

	@Override
	public String toString() {
		return String.format("SaveIntervalButton [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
