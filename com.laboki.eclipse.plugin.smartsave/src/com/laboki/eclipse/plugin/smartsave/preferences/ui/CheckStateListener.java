package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;

import com.laboki.eclipse.plugin.smartsave.events.AddContentTypeToBlacklistEvent;
import com.laboki.eclipse.plugin.smartsave.events.RemoveContentTypeFromBlacklistEvent;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;

public class CheckStateListener implements ICheckStateListener {

	@Override
	public void
	checkStateChanged(final CheckStateChangedEvent event) {
		if (event.getChecked()) CheckStateListener.removeFromBlacklist(event);
		else CheckStateListener.addToBlacklist(event);
	}

	private static void
	removeFromBlacklist(final CheckStateChangedEvent event) {
		EventBus.post(CheckStateListener.removeEvent(event));
	}

	private static RemoveContentTypeFromBlacklistEvent
	removeEvent(final CheckStateChangedEvent event) {
		return new RemoveContentTypeFromBlacklistEvent(((IContentType) event.getElement()).getId());
	}

	private static void
	addToBlacklist(final CheckStateChangedEvent event) {
		EventBus.post(CheckStateListener.addEvent(event));
	}

	private static AddContentTypeToBlacklistEvent
	addEvent(final CheckStateChangedEvent event) {
		return new AddContentTypeToBlacklistEvent(((IContentType) event.getElement()).getId());
	}
}
