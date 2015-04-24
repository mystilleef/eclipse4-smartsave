package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.PreferencesWidgetDisposedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;

public final class ContentTypeTableViewer implements Instance {

	private static final CheckStateListener LISTENER = new CheckStateListener();
	private CheckboxTableViewer viewer;
	private final ContentTypeBlacklistUpdater blacklistUpdater =
		new ContentTypeBlacklistUpdater();

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

	private static boolean
	isBlacklisted(final ArrayList<String> blacklist, final Object element) {
		return blacklist.contains(((IContentType) element).getId());
	}

	@Override
	public Instance
	start() {
		EventBus.register(this);
		this.blacklistUpdater.start();
		this.viewer.getControl().setFocus();
		this.viewer.refresh();
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
}
