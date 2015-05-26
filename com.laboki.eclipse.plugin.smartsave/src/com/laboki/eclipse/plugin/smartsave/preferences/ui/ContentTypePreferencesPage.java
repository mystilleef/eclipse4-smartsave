package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.laboki.eclipse.plugin.smartsave.events.PreferencesWidgetDisposedEvent;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;

public final class ContentTypePreferencesPage extends PreferencePage
	implements
		IWorkbenchPreferencePage {

	public ContentTypePreferencesPage() {}

	public ContentTypePreferencesPage(final String title) {
		super(title);
	}

	public ContentTypePreferencesPage(
		final String title,
		final ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void
	init(final IWorkbench workbench) {}

	@Override
	protected Control
	createContents(final Composite parent) {
		final Composite composite = Util.createComposite(parent, 1);
		ContentTypePreferencesPage.createLabel(composite);
		ContentTypePreferencesPage.createSearchField(composite);
		ContentTypePreferencesPage.createListView(composite);
		parent.layout();
		parent.pack();
		return composite;
	}

	private static void
	createLabel(final Composite parent) {
		final Label label = new Label(parent, SWT.WRAP);
		final String text =
			"Enable or disable smart save for specific file types.";
		final GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = parent.getMonitor().getClientArea().width / 8;
		label.setLayoutData(data);
		label.setText(text);
		parent.layout();
		parent.pack();
	}

	private static void
	createSearchField(final Composite parent) {
		new ContentTypeText(parent).start();
		parent.layout();
		parent.pack();
	}

	private static void
	createListView(final Composite parent) {
		new ContentTypeTableViewer(parent).start();
		parent.layout();
		parent.pack();
	}

	@Override
	public void
	dispose() {
		EventBus.post(new PreferencesWidgetDisposedEvent());
		super.dispose();
	}
}
