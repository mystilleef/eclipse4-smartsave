package com.laboki.eclipse.plugin.smartsave.preferences.ui;

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

import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;

public final class PreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final int FONT_SIZE = 12;
	private static Composite pageComposite;
	private final EventBus eventBus = EditorContext.EVENT_BUS;

	@Override
	protected Control createContents(final Composite parent) {
		PreferencesPage.pageComposite = PreferencesPage.createPageComposite(parent);
		this.createSaveAutomaticallySection();
		this.createWarningErrorSection();
		this.createSaveIntervalSections();
		return PreferencesPage.pageComposite;
	}

	private static Composite createPageComposite(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return composite;
	}

	private void createSaveAutomaticallySection() {
		PreferencesPage.createSectionLabel("Toggle Smart Saving");
		final Composite composite = PreferencesPage.createHorizontalLayoutComposite();
		PreferencesPage.createLabel(composite, "&Save files automatically: ");
		new SaveResponseComboViewer(composite, this.eventBus).begin();
		PreferencesPage.separator(PreferencesPage.pageComposite);
	}

	private void createWarningErrorSection() {
		PreferencesPage.separator(PreferencesPage.pageComposite);
		PreferencesPage.createSectionLabel("Errors and Warnings");
		final Composite composite = PreferencesPage.createHorizontalLayoutComposite();
		this.createErrorComboView(composite, "Save files when &errors are present: ");
		this.createWarningComboView(composite, "Save files when &warnings are present: ");
		PreferencesPage.separator(PreferencesPage.pageComposite);
	}

	private void createErrorComboView(final Composite composite, final String name) {
		PreferencesPage.createLabel(composite, name);
		new ErrorResponseComboViewer(composite, this.eventBus).begin();
	}

	private void createWarningComboView(final Composite composite, final String name) {
		PreferencesPage.createLabel(composite, name);
		new WarningResponseComboViewer(composite, this.eventBus).begin();
	}

	private void createSaveIntervalSections() {
		PreferencesPage.separator(PreferencesPage.pageComposite);
		PreferencesPage.createSectionLabel("Save Interval");
		final Composite composite = PreferencesPage.createHorizontalLayoutComposite();
		PreferencesPage.createLabel(composite, "If possible try to save &files every: ");
		new SaveIntervalButton(composite, this.eventBus).begin();
	}

	private static void createLabel(final Composite composite, final String name) {
		final Label label = new Label(composite, SWT.NONE);
		label.setText(name);
	}

	private static Composite createHorizontalLayoutComposite() {
		final Composite layoutComposite = new Composite(PreferencesPage.pageComposite, SWT.NONE);
		layoutComposite.setLayout(new GridLayout(2, false));
		layoutComposite.setLayoutData(PreferencesPage.createHorizontalDataGrid());
		return layoutComposite;
	}

	private static GridData createHorizontalDataGrid() {
		final GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
		return data;
	}

	private static void createSectionLabel(final String title) {
		final Composite composite = new Composite(PreferencesPage.pageComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		PreferencesPage.newSectionLabel(composite, title);
	}

	private static void newSectionLabel(final Composite composite, final String title) {
		final Label label = new Label(composite, SWT.None);
		label.setText(title);
		label.setFont(new Font(PreferencesPage.pageComposite.getDisplay(), label.getFont().getFontData()[0].getName(), PreferencesPage.FONT_SIZE, SWT.BOLD));
	}

	private static void separator(final Composite composite) {
		final Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	public void init(final IWorkbench workbench) {}

	@Override
	protected void performDefaults() {
		Store.clear();
	}
}
