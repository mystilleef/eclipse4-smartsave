package com.laboki.eclipse.plugin.smartsave.events;


public final class RemoveContentTypeFromBlacklistEvent {

	private final String string;

	public RemoveContentTypeFromBlacklistEvent(final String string) {
		this.string = string;
	}

	public String
	getContentTypeId() {
		return this.string;
	}
}
