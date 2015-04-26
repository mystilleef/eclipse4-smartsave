package com.laboki.eclipse.plugin.smartsave.events;

public class ContentTypeSearchQueryEvent {

	private final String query;

	public ContentTypeSearchQueryEvent(final String query) {
		this.query = query;
	}

	public String
	getQuery() {
		return this.query;
	}
}
