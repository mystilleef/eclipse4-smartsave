package com.laboki.eclipse.plugin.smartsave.events;

public class AddContentTypeToBlacklistEvent {

	private final String string;

	public AddContentTypeToBlacklistEvent(final String string) {
		this.string = string;
	}

	public String
	getContentTypeId() {
		return this.string;
	}
}
