package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.laboki.eclipse.plugin.smartsave.events.FocusSaveIntervalDialogSpinnerEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;

final class SaveIntervalDialog extends AbstractEventBusInstance {

	private static final int SPINNER_GRID_LAYOUT_COLUMNS = 3;
	private static final int MARGIN_SIZE = 10;
	final Shell dialog;

	public SaveIntervalDialog(final Composite composite) {
		super();
		this.dialog =
			new Shell(composite.getShell(), SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);
	}

	@Override
	public Instance
	start() {
		this.setupDialog();
		this.arrangeWidgets();
		return super.start();
	}

	private void
	setupDialog() {
		this.dialog.setLayout(SaveIntervalDialog.createLayout());
		this.dialog.setText("Save Interval");
		this.dialog.addShellListener(new DialogShellListener());
	}

	private static GridLayout
	createLayout() {
		final GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = SaveIntervalDialog.MARGIN_SIZE;
		layout.marginTop = SaveIntervalDialog.MARGIN_SIZE;
		layout.marginLeft = SaveIntervalDialog.MARGIN_SIZE;
		layout.marginRight = SaveIntervalDialog.MARGIN_SIZE;
		return layout;
	}

	private void
	arrangeWidgets() {
		this.addLabel();
		this.addSpinnerSection();
		this.dialog.pack();
	}

	private void
	addLabel() {
		final String text = "Press ESC or ENTER to close window.";
		final StyledText fieldText =
			new StyledText(this.dialog, SWT.LEFT | SWT.WRAP | SWT.READ_ONLY);
		this.setLabelProperties(text, fieldText);
		SaveIntervalDialog.setLabelStyle(text, fieldText);
	}

	private void
	setLabelProperties(final String text, final StyledText fieldText) {
		fieldText.setText(text);
		fieldText.setEditable(false);
		fieldText.setCaret(null);
		fieldText.setBackground(this.dialog.getBackground());
		fieldText.setLayoutData(new GridData());
	}

	private static void
	setLabelStyle(final String text, final StyledText fieldText) {
		final StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = text.length();
		styleRange.fontStyle = SWT.BOLD;
		fieldText.setStyleRange(styleRange);
	}

	private void
	addSpinnerSection() {
		final Composite composite = this.createSpinnerComposite();
		SaveIntervalDialog.createLabel(composite, "Save files every ");
		new SaveIntervalDialogSpinner(composite).start();
		SaveIntervalDialog.createLabel(composite, " seconds");
	}

	private static void
	createLabel(final Composite composite, final String name) {
		final Label label = new Label(composite, SWT.NONE);
		label.setText(name);
	}

	private Composite
	createSpinnerComposite() {
		final Composite composite = new Composite(this.dialog, SWT.NONE);
		composite
			.setLayout(new GridLayout(SaveIntervalDialog.SPINNER_GRID_LAYOUT_COLUMNS,
				false));
		composite.setLayoutData(new GridData());
		return composite;
	}

	public void
	show() {
		this.dialog.open();
	}

	@Override
	public Instance
	stop() {
		this.dialog.dispose();
		return super.stop();
	}

	private final class DialogShellListener implements ShellListener {

		public DialogShellListener() {}

		@Override
		public void
		shellActivated(final ShellEvent arg0) {
			EventBus.post(new FocusSaveIntervalDialogSpinnerEvent());
		}

		@Override
		public void
		shellClosed(final ShellEvent event) {
			event.doit = false;
			SaveIntervalDialog.this.dialog.setVisible(false);
		}

		@Override
		public void
		shellDeactivated(final ShellEvent arg0) {}

		@Override
		public void
		shellDeiconified(final ShellEvent arg0) {}

		@Override
		public void
		shellIconified(final ShellEvent arg0) {}
	}
}
