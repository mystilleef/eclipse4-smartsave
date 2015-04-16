package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

abstract class PreferencesResponseComboViewer extends ResponseComboViewer
	implements
		Instance {

	private static final String TASK_NAME =
		"preferences response combo viewer task";
	private static final TaskMutexRule RULE = new TaskMutexRule();
	protected static final int YES = 0;
	protected static final int NO = 1;

	protected PreferencesResponseComboViewer(final Composite parent) {
		super(parent);
	}

	@Override
	protected void
	handleResponseSelection(final SelectionChangedEvent event) {}

	protected boolean
	getSelectionValue(final SelectionChangedEvent event) {
		super.handleResponseSelection(event);
		return ((Response) ((IStructuredSelection) event.getSelection()).getFirstElement()).value();
	}

	private void
	updateComboProperties() {
		this.updateSelection();
	}

	protected void
	updateSelection() {}

	protected void
	setSelection(final int index) {
		this.stopListening();
		this.setSelection(new StructuredSelection(this.getResponses()[index]));
		this.startListening();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void
	preferencesChanged(final PreferenceStoreChangeEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				PreferencesResponseComboViewer.this.updateSelection();
			}
		}.setName(PreferencesResponseComboViewer.TASK_NAME)
			.setRule(PreferencesResponseComboViewer.RULE)
			.start();
	}

	@Override
	public Instance
	start() {
		EventBus.register(this);
		this.startListening();
		this.updateComboProperties();
		return this;
	}

	@Override
	public Instance
	stop() {
		EventBus.unregister(this);
		this.stopListening();
		return this;
	}
}
