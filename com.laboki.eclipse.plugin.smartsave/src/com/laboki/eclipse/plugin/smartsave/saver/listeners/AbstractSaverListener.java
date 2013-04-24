package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.Task;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;
import com.laboki.eclipse.plugin.smartsave.saver.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.EnableSaveListenersEvent;

public abstract class AbstractSaverListener implements ISaverListener, Instance {

	private final EventBus eventBus;

	public AbstractSaverListener(final EventBus eventbus) {
		this.eventBus = eventbus;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void addListener(@SuppressWarnings("unused") final EnableSaveListenersEvent event) {
		EditorContext.asyncExec(new Task("") {

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
			e.printStackTrace();
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void removeListener(@SuppressWarnings("unused") final DisableSaveListenersEvent event) {
		EditorContext.asyncExec(new Task("") {

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
			e.printStackTrace();
		}
	}

	protected void scheduleSave() {
		EditorContext.scheduleSave(this.eventBus);
	}
}
