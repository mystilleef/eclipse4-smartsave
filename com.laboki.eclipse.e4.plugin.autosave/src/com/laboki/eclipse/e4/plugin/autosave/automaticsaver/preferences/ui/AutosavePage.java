package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.ui;

import org.eclipse.jface.preference.ColorFieldEditor;
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
		this.createColorEditor(pageComposite);
		AutosavePage.createComboView(pageComposite);
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

	private void createColorEditor(final Composite pageComposite) {
		final ColorFieldEditor colorEditor = new ColorFieldEditor("Blue", "Highlight Color", AutosavePage.createHorizontalLayoutComposite(pageComposite));
		colorEditor.setPage(this);
		colorEditor.load();
	}

	private static Composite createHorizontalLayoutComposite(final Composite pageComposite) {
		final Composite layoutComposite = new Composite(pageComposite, SWT.NONE);
		layoutComposite.setLayout(new GridLayout());
		layoutComposite.setLayoutData(AutosavePage.createHorizontalDataGrid());
		return layoutComposite;
	}

	private static void createComboView(final Composite pageComposite) {
		final Composite composite = AutosavePage.createHorizontalLayoutComposite(pageComposite);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Save files automatically:");
		new SaveResponseComboViewer(composite).startListening();
	}

	@Override
	public void init(final IWorkbench workbench) {}

	@Override
	protected void performDefaults() {
		PreferencesStore.clear();
	}
}
