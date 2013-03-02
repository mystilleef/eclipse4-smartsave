package com.laboki.eclipse.plugin.smartsave.saver.preferences.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.laboki.eclipse.plugin.smartsave.saver.preferences.PreferencesStore;

public final class AutosavePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final int FONT_SIZE = 12;
	private static Composite pageComposite;

	@Override
	protected Control createContents(final Composite parent) {
		AutosavePage.pageComposite = AutosavePage.createPageComposite(parent);
		AutosavePage.createSaveAutomaticallySection();
		AutosavePage.createWarningErrorSection();
		AutosavePage.createSaveIntervalSections();
		return AutosavePage.pageComposite;
	}

	private static Composite createPageComposite(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return composite;
	}

	private static void createSaveAutomaticallySection() {
		AutosavePage.createSectionLabel("Toggle Automatic Saving");
		final Composite composite = AutosavePage.createHorizontalLayoutComposite();
		AutosavePage.createLabel(composite, "&Save files automatically: ");
		new SaveResponseComboViewer(composite).startListening();
		AutosavePage.separator(AutosavePage.pageComposite);
	}

	private static void createWarningErrorSection() {
		AutosavePage.separator(AutosavePage.pageComposite);
		AutosavePage.createSectionLabel("Errors and Warnings");
		final Composite composite = AutosavePage.createHorizontalLayoutComposite();
		AutosavePage.createErrorComboView(composite, "Save files when &errors are present: ");
		AutosavePage.createWarningComboView(composite, "Save files when &warnings are present: ");
		AutosavePage.separator(AutosavePage.pageComposite);
	}

	private static void createErrorComboView(final Composite composite, final String name) {
		AutosavePage.createLabel(composite, name);
		new ErrorResponseComboViewer(composite).startListening();
	}

	private static void createWarningComboView(final Composite composite, final String name) {
		AutosavePage.createLabel(composite, name);
		new WarningResponseComboViewer(composite).startListening();
	}

	private static void createSaveIntervalSections() {
		AutosavePage.separator(AutosavePage.pageComposite);
		AutosavePage.createSectionLabel("Save Interval");
		final Composite composite = AutosavePage.createHorizontalLayoutComposite();
		AutosavePage.createLabel(composite, "Save &files every: ");
		new SaveIntervalButton(composite).startListening();
	}

	private static void createLabel(final Composite composite, final String name) {
		final Label label = new Label(composite, SWT.NONE);
		label.setText(name);
	}

	private static Composite createHorizontalLayoutComposite() {
		final Composite layoutComposite = new Composite(AutosavePage.pageComposite, SWT.NONE);
		layoutComposite.setLayout(new GridLayout(2, false)); // $codepro.audit.disable numericLiterals
		layoutComposite.setLayoutData(AutosavePage.createHorizontalDataGrid());
		return layoutComposite;
	}

	private static GridData createHorizontalDataGrid() {
		final GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
		return data;
	}

	private static void createSectionLabel(final String title) {
		final Composite composite = new Composite(AutosavePage.pageComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		AutosavePage.newSectionLabel(composite, title);
	}

	private static void newSectionLabel(final Composite composite, final String title) {
		final Label label = new Label(composite, SWT.None);
		label.setText(title);
		label.setFont(new Font(AutosavePage.pageComposite.getDisplay(), label.getFont().getFontData()[0].getName(), AutosavePage.FONT_SIZE, SWT.BOLD));
	}

	private static void separator(final Composite composite) {
		final Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	public void init(final IWorkbench workbench) {}

	@Override
	protected void performDefaults() {
		PreferencesStore.clear();
	}
}
