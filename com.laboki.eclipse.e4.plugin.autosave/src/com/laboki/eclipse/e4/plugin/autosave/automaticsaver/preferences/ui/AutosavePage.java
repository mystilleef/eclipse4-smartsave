package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.PreferencesStore;

public final class AutosavePage extends PreferencePage implements IWorkbenchPreferencePage {

	@Override
	protected Control createContents(final Composite parent) {
		final Composite pageComposite = AutosavePage.createPageComposite(parent);
		AutosavePage.createSaveComboView(pageComposite, "Save files Automatically: ");
		AutosavePage.createErrorComboView(pageComposite, "Save files when errors are present: ");
		AutosavePage.createWarningComboView(pageComposite, "Save files when warnings are present: ");
		return pageComposite;
	}

	private static Composite createPageComposite(final Composite parent) {
		final Composite pageComposite = new Composite(parent, SWT.NULL);
		pageComposite.setLayout(new GridLayout());
		pageComposite.setLayoutData(AutosavePage.createHorizontalDataGrid());
		return pageComposite;
	}

	private static GridData createHorizontalDataGrid() {
		final GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		return data;
	}

	private static Composite createHorizontalLayoutComposite(final Composite pageComposite) {
		final Composite layoutComposite = new Composite(pageComposite, SWT.NONE);
		layoutComposite.setLayout(new GridLayout());
		layoutComposite.setLayoutData(AutosavePage.createHorizontalDataGrid());
		return layoutComposite;
	}

	private static void createSaveComboView(final Composite pageComposite, final String name) {
		final Composite composite = AutosavePage.createHorizontalLayoutComposite(pageComposite);
		AutosavePage.createLabel(composite, name);
		new SaveResponseComboViewer(composite).startListening();
	}

	private static void createErrorComboView(final Composite pageComposite, final String name) {
		final Composite composite = AutosavePage.createHorizontalLayoutComposite(pageComposite);
		AutosavePage.createLabel(composite, name);
		new ErrorResponseComboViewer(composite).startListening();
	}

	private static void createWarningComboView(final Composite pageComposite, final String name) {
		final Composite composite = AutosavePage.createHorizontalLayoutComposite(pageComposite);
		AutosavePage.createLabel(composite, name);
		new WarningResponseComboViewer(composite).startListening();
	}

	private static void createLabel(final Composite composite, final String name) {
		final Label label = new Label(composite, SWT.NONE);
		label.setText(name);
	}

	@Override
	public void init(final IWorkbench workbench) {}

	@Override
	protected void performDefaults() {
		PreferencesStore.clear();
	}
}
