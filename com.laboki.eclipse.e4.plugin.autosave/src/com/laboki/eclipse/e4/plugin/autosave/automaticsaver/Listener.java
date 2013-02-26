package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

final class Listener implements EventHandler {

	private final String eventName;
	private final ListenerHandler handler;
	private final IEventBroker eventBroker;
	private boolean isListening;

	Listener(final String event, final ListenerHandler handler, final IEventBroker eventBroker) {
		this.eventName = event;
		this.handler = handler;
		this.eventBroker = eventBroker;
	}

	void start() {
		if (this.isListening) return;
		final boolean result = this.eventBroker.subscribe(this.eventName, this);
		if (result) this.isListening = true;
	}

	void stop() {
		if (!this.isListening) return;
		final boolean result = this.eventBroker.unsubscribe(this);
		if (result) this.isListening = false;
	}

	protected ListenerHandler getHandler() {
		return this.handler;
	}

	@Override
	public void handleEvent(final Event event) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				Listener.this.getHandler().handle(event);
			}
		});
	}
}
