package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.laboki.eclipse.plugin.smartsave.events.ContentTypeSearchQueryEvent;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class ContentTypeText implements Instance {

	private static final TaskMutexRule RULE = new TaskMutexRule();
	protected final Text text;

	public ContentTypeText(final Composite parent) {
		this.text =
			new Text(parent, SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		this.setLayout();
		this.setProperties();
		this.addListeners();
	}

	private void
	setLayout() {
		final GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessVerticalSpace = false;
		this.text.setLayoutData(data);
	}

	private void
	setProperties() {
		this.text.setMessage("start typing to filter file types...");
	}

	private void
	addListeners() {
		this.text.addModifyListener(e -> {
			new AsyncTask() {

				@Override
				public void
				execute() {
					EventBus.post(new ContentTypeSearchQueryEvent(this.getText()));
				}

				private String
				getText() {
					return ContentTypeText.this.text.getText().trim();
				}
			}.setRule(ContentTypeText.RULE).start();
		});
	}

	@Override
	public Instance
	start() {
		return this;
	}

	@Override
	public Instance
	stop() {
		return this;
	}
}
