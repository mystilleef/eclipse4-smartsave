package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.ContentFilterQueryUpdatedEvent;
import com.laboki.eclipse.plugin.smartsave.events.PreferencesWidgetDisposedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class ContentTypeTableViewer implements Instance {

	static final CheckStateListener LISTENER = new CheckStateListener();
	protected CheckboxTableViewer viewer;
	private final ContentTypeBlacklistUpdater blacklistUpdater =
		new ContentTypeBlacklistUpdater();
	private final ContentTypeFilter searchFilter = new ContentTypeFilter();

	public ContentTypeTableViewer(final Composite parent) {
		this.viewer =
			CheckboxTableViewer.newCheckList(parent, SWT.H_SCROLL
				| SWT.V_SCROLL
				| SWT.FULL_SELECTION
				| SWT.BORDER
				| SWT.MULTI);
		this.setlayout();
		this.setProperties();
		this.setDefaultChecks();
	}

	private void
	setlayout() {
		final GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.heightHint = this.getHeightHint();
		this.viewer.getTable().setLayoutData(data);
	}

	private int
	getHeightHint() {
		return this.viewer.getControl().getShell().getMonitor().getClientArea().height / 8;
	}

	private void
	setProperties() {
		this.viewer.setContentProvider(ArrayContentProvider.getInstance());
		this.viewer.setFilters(new ViewerFilter[] {
			this.searchFilter
		});
		this.viewer.setInput(EditorContext.getContentTypes());
		this.viewer.setLabelProvider(new ContentTypeLabelProvider());
		this.viewer.getTable().setLinesVisible(true);
	}

	private void
	setDefaultChecks() {
		this.viewer.setAllChecked(true);
		final ArrayList<String> blacklist = EditorContext.getBlacklist();
		for (final Object element : this.viewer.getCheckedElements())
			if (ContentTypeTableViewer.isBlacklisted(blacklist, element)) this.viewer.setChecked(element,
				false);
	}

	static boolean
	isBlacklisted(final ArrayList<String> blacklist, final Object element) {
		return blacklist.contains(((IContentType) element).getId());
	}

	@Override
	public Instance
	start() {
		EventBus.register(this);
		this.blacklistUpdater.start();
		this.searchFilter.start();
		this.viewer.refresh();
		this.viewer.getControl().setFocus();
		this.addListeners();
		return this;
	}

	private void
	addListeners() {
		this.viewer.addCheckStateListener(ContentTypeTableViewer.LISTENER);
	}

	@Override
	public Instance
	stop() {
		EventBus.unregister(this);
		this.searchFilter.stop();
		this.blacklistUpdater.stop();
		this.removeListeners();
		this.viewer = null;
		return this;
	}

	private void
	removeListeners() {
		this.viewer.removeCheckStateListener(ContentTypeTableViewer.LISTENER);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final PreferencesWidgetDisposedEvent event) {
		this.stop();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final ContentFilterQueryUpdatedEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				ContentTypeTableViewer.this.viewer.refresh(true, true);
				this.toggleChecks();
			}

			private void
			toggleChecks() {
				ContentTypeTableViewer.this.viewer.removeCheckStateListener(ContentTypeTableViewer.LISTENER);
				this.iterateElements();
				ContentTypeTableViewer.this.viewer.addCheckStateListener(ContentTypeTableViewer.LISTENER);
			}

			private void
			iterateElements() {
				final ArrayList<String> blacklist = EditorContext.getBlacklist();
				for (final TableItem tableItem : ContentTypeTableViewer.this.viewer.getTable()
					.getItems())
					this.toggleElement(blacklist, tableItem.getData());
			}

			private void
			toggleElement(final ArrayList<String> blacklist, final Object element) {
				if (ContentTypeTableViewer.isBlacklisted(blacklist, element)) this.uncheck(element);
				else this.check(element);
			}

			private void
			uncheck(final Object element) {
				ContentTypeTableViewer.this.viewer.setChecked(element, false);
			}

			private void
			check(final Object element) {
				ContentTypeTableViewer.this.viewer.setChecked(element, true);
			}
		}.start();
	}
}
