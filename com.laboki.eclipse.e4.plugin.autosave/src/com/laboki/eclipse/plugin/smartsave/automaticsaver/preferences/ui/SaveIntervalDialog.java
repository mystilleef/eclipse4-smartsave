package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

final class SaveIntervalDialog {

	private static final int SPINNER_GRID_LAYOUT_COLUMNS = 3;
	private static final int MARGIN_SIZE = 10;
	private static Shell dialog;

	public SaveIntervalDialog(final Composite composite) {
		SaveIntervalDialog.dialog = new Shell(composite.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		SaveIntervalDialog.updateProperties();
	}

	private static void updateProperties() {
		SaveIntervalDialog.getDialog().setLayout(SaveIntervalDialog.createLayout());
		SaveIntervalDialog.getDialog().setText("Save Interval");
		SaveIntervalDialog.addLabel();
		SaveIntervalDialog.addSpinnerSection();
		SaveIntervalDialog.getDialog().pack();
	}

	private static GridLayout createLayout() {
		final GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = SaveIntervalDialog.MARGIN_SIZE;
		layout.marginTop = SaveIntervalDialog.MARGIN_SIZE;
		layout.marginLeft = SaveIntervalDialog.MARGIN_SIZE;
		layout.marginRight = SaveIntervalDialog.MARGIN_SIZE;
		return layout;
	}

	private static void addLabel() {
		final String text = "Press ESC or ENTER to close window.";
		final StyledText fieldText = new StyledText(SaveIntervalDialog.getDialog(), SWT.LEFT | SWT.WRAP | SWT.READ_ONLY);
		SaveIntervalDialog.setLabelProperties(text, fieldText);
		SaveIntervalDialog.setLabelStyle(text, fieldText);
	}

	private static void setLabelProperties(final String text, final StyledText fieldText) {
		fieldText.setText(text);
		fieldText.setEditable(false);
		fieldText.setCaret(null);
		fieldText.setBackground(SaveIntervalDialog.getDialog().getBackground());
		fieldText.setLayoutData(new GridData());
	}

	private static void setLabelStyle(final String text, final StyledText fieldText) {
		final StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = text.length();
		styleRange.fontStyle = SWT.BOLD;
		fieldText.setStyleRange(styleRange);
	}

	private static void addSpinnerSection() {
		final Composite composite = SaveIntervalDialog.createSpinnerComposite();
		SaveIntervalDialog.createLabel(composite, "Save files every ");
		new SaveIntervalDialogSpinner(composite).startListening();
		SaveIntervalDialog.createLabel(composite, " seconds");
	}

	private static void createLabel(final Composite composite, final String name) {
		final Label label = new Label(composite, SWT.NONE);
		label.setText(name);
	}

	private static Composite createSpinnerComposite() {
		final Composite composite = new Composite(SaveIntervalDialog.getDialog(), SWT.NONE);
		composite.setLayout(new GridLayout(SaveIntervalDialog.SPINNER_GRID_LAYOUT_COLUMNS, false));
		composite.setLayoutData(new GridData());
		return composite;
	}

	public static void show() {
		SaveIntervalDialog.getDialog().open();
	}

	public static Shell getDialog() {
		return SaveIntervalDialog.dialog;
	}

	@Override
	public String toString() {
		return String.format("SaveIntervalDialog [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
