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

abstract class PreferencesResponseComboViewer extends ResponseComboViewer implements Instance {

	protected static final int YES = 0;
	protected static final int NO = 1;
	private final EventBus eventBus;

	protected PreferencesResponseComboViewer(final Composite parent, final EventBus eventBus) {
		super(parent);
		this.eventBus = eventBus;
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {}

	protected boolean getSelectionValue(final SelectionChangedEvent event) {
		super.handleResponseSelection(event);
		return ((Response) ((IStructuredSelection) event.getSelection()).getFirstElement()).value();
	}

	private void updateComboProperties() {
		this.updateSelection();
	}

	protected void updateSelection() {}

	protected void setSelection(final int index) {
		this.stopListening();
		this.setSelection(new StructuredSelection(this.getResponses()[index]));
		this.startListening();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void preferencesChanged(@SuppressWarnings("unused") final PreferenceStoreChangeEvent event) {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				PreferencesResponseComboViewer.this.updateSelection();
			}
		}.begin();
	}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		this.startListening();
		this.updateComboProperties();
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		this.stopListening();
		return this;
	}
}
