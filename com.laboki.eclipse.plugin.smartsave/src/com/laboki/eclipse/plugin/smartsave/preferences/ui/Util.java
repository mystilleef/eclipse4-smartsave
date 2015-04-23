package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public enum Util {
	INSTANCE;

	/**
	 * Creates composite control and sets the default layout data.
	 *
	 * @param parent the parent of the new composite
	 * @param numColumns the number of columns for the new composite
	 * @return the newly-created composite
	 */
	public static Composite
	createComposite(final Composite parent, final int numColumns) {
		final Composite composite = new Composite(parent, SWT.NULL);
		// GridLayout
		final GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
		// GridData
		final GridData data = new GridData();
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		composite.setLayoutData(data);
		return composite;
	}

	/**
	 * Utility method that creates a label instance and sets the default
	 * layout data.
	 *
	 * @param parent the parent for the new label
	 * @param text the text for the new label
	 * @return the new label
	 */
	public static Label
	createLabel(final Composite parent, final String text) {
		final Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		final GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);
		return label;
	}
}
