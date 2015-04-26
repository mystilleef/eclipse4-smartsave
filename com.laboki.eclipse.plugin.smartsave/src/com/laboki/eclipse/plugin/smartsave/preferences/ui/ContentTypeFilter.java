package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.ContentFilterQueryUpdatedEvent;
import com.laboki.eclipse.plugin.smartsave.events.ContentTypeSearchQueryEvent;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class ContentTypeFilter extends ViewerFilter implements Instance {

	private String query;
	private static final TaskMutexRule RULE = new TaskMutexRule();
	private static final int PATTERN_FLAGS = Pattern.CASE_INSENSITIVE
		| Pattern.CANON_EQ
		| Pattern.UNICODE_CASE;

	public ContentTypeFilter() {}

	public void
	setSearchText(final String s) {
		this.query = ".*" + Pattern.quote(s) + ".*";
		EventBus.post(new ContentFilterQueryUpdatedEvent());
	}

	@Subscribe
	public void
	eventHandler(final ContentTypeSearchQueryEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				ContentTypeFilter.this.setSearchText(event.getQuery());
			}
		}.setRule(ContentTypeFilter.RULE).start();
	}

	@Override
	public boolean
	select(final Viewer viewer, final Object parentElement, final Object element) {
		if ((this.query == null) || (this.query.length() == 0)) return true;
		final IContentType type = (IContentType) element;
		if (this.matchFound(type.getName())) return true;
		if (this.matchFound(type.getId())) return true;
		return false;
	}

	private boolean
	matchFound(final String s) {
		if (Pattern.compile(this.query, ContentTypeFilter.PATTERN_FLAGS)
			.matcher(s)
			.find()) return true;
		return false;
	}

	@Override
	public Instance
	start() {
		EventBus.register(this);
		return this;
	}

	@Override
	public Instance
	stop() {
		EventBus.unregister(this);
		return this;
	}
}
