package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;

public final class PreferencesPage extends PreferencePage
	implements
		IWorkbenchPreferencePage {

	private Composite pageComposite;

	@Override
	protected Control
	createContents(final Composite parent) {
		this.pageComposite = this.createPageComposite(parent);
		this.createSaveAutomaticallySection();
		this.createWarningErrorSection();
		this.createSaveIntervalSections();
		return this.pageComposite;
	}

	@SuppressWarnings("static-method")
	private Composite
	createPageComposite(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return composite;
	}

	private void
	createSaveAutomaticallySection() {
		this.createSectionLabel("Toggle Smart Saving");
		final Composite composite = this.createHorizontalLayoutComposite();
		PreferencesPage.createLabel(composite, "&Save files automatically: ");
		new SaveResponseComboViewer(composite).start();
	}

	private void
	createWarningErrorSection() {
		this.createSectionLabel("Errors and Warnings");
		final Composite composite = this.createHorizontalLayoutComposite();
		PreferencesPage.createErrorComboView(composite,
			"Save files when &errors are present: ");
		PreferencesPage.createWarningComboView(composite,
			"Save files when &warnings are present: ");
	}

	private static void
	createErrorComboView(final Composite composite, final String name) {
		PreferencesPage.createLabel(composite, name);
		new ErrorResponseComboViewer(composite).start();
	}

	private static void
	createWarningComboView(final Composite composite, final String name) {
		PreferencesPage.createLabel(composite, name);
		new WarningResponseComboViewer(composite).start();
	}

	private void
	createSaveIntervalSections() {
		this.createSectionLabel("Save Interval");
		final Composite composite = this.createHorizontalLayoutComposite();
		PreferencesPage.createLabel(composite,
			"If possible try to save &files every: ");
		new SaveIntervalButton(composite).start();
	}

	private static void
	createLabel(final Composite composite, final String name) {
		final Label label = new Label(composite, SWT.NONE);
		label.setText(name);
	}

	private Composite
	createHorizontalLayoutComposite() {
		final Composite composite = new Composite(this.pageComposite, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(PreferencesPage.createHorizontalDataGrid());
		return composite;
	}

	private static GridData
	createHorizontalDataGrid() {
		return new GridData(SWT.FILL, SWT.NONE, true, false);
	}

	private void
	createSectionLabel(final String title) {
		final Composite composite = new Composite(this.pageComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		PreferencesPage.newSectionLabel(composite, title);
	}

	private static void
	newSectionLabel(final Composite composite, final String title) {
		final Label label = new Label(composite, SWT.None);
		label.setText(title);
		label.setFont(FONT.LARGE_BOLD_FONT);
	}

	@Override
	public void
	init(final IWorkbench workbench) {}

	@Override
	protected void
	performDefaults() {
		Store.clear();
	}

	@Override
	public void
	dispose() {
		// EventBus.post(new PreferencesWidgetDisposedEvent());
		super.dispose();
	}

	private enum FONT {
		INSTANCE;

		public static final Font LARGE_BOLD_FONT = FONT.newLargeBoldFont();

		private static Font
		newLargeBoldFont() {
			return new Font(EditorContext.DISPLAY,
				FONT.getDefaultFontName(),
				FONT.getDefaultFontHeight() + 2,
				SWT.BOLD);
		}

		private static String
		getDefaultFontName() {
			return FONT.getDefaultFontData().get().getName();
		}

		private static int
		getDefaultFontHeight() {
			return FONT.getDefaultFontData().get().getHeight();
		}

		private static Optional<FontData>
		getDefaultFontData() {
			final Optional<Shell> shell = EditorContext.getShell();
			if (!shell.isPresent()) return Optional.absent();
			return Optional.fromNullable(shell.get().getFont().getFontData()[0]);
		}
	}
}
