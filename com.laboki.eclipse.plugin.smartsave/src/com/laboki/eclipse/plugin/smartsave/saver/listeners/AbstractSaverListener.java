package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import java.util.logging.Level;

import lombok.extern.java.Log;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;
import com.laboki.eclipse.plugin.smartsave.saver.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.EnableSaveListenersEvent;

@Log
public abstract class AbstractSaverListener implements ISaverListener, Instance {

	private static final Level FINEST = Level.FINEST;
	private final EventBus eventBus;

	public AbstractSaverListener(final EventBus eventbus) {
		this.eventBus = eventbus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void addListener(@SuppressWarnings("unused") final EnableSaveListenersEvent event) {
		EditorContext.asyncExec(new AsyncTask("") {

			@Override
			public void execute() {
				AbstractSaverListener.this.tryToAdd();
			}
		});
	}

	private void tryToAdd() {
		try {
			this.add();
		} catch (final Exception e) {
			AbstractSaverListener.log.log(AbstractSaverListener.FINEST, "failed to add listener");
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void removeListener(@SuppressWarnings("unused") final DisableSaveListenersEvent event) {
		EditorContext.asyncExec(new AsyncTask("") {

			@Override
			public void execute() {
				AbstractSaverListener.this.tryToRemove();
			}
		});
	}

	@Override
	public void add() {}

	@Override
	public void remove() {}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		return this;
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		this.tryToRemove();
		return this;
	}

	private void tryToRemove() {
		try {
			this.remove();
		} catch (final Exception e) {
			AbstractSaverListener.log.log(AbstractSaverListener.FINEST, "failed to remove listener");
		}
	}

	protected void scheduleSave() {
		EditorContext.scheduleSave(this.eventBus);
	}
}
