package com.laboki.eclipse.plugin.smartsave.listeners.abstraction;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public abstract class AbstractListener extends AbstractEventBusInstance implements IListener {

	public AbstractListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void addListener(@SuppressWarnings("unused") final EnableSaveListenersEvent event) {
		new AsyncTask(EditorContext.LISTENER_SAVER_TASK) {

			@Override
			public void asyncExecute() {
				AbstractListener.this.tryToAdd();
			}
		}.begin();
	}

	private void tryToAdd() {
		try {
			this.add();
		} catch (final Exception e) {}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void removeListener(@SuppressWarnings("unused") final DisableSaveListenersEvent event) {
		new AsyncTask(EditorContext.LISTENER_SAVER_TASK) {

			@Override
			public void asyncExecute() {
				AbstractListener.this.tryToRemove();
			}
		}.begin();
	}

	@Override
	public void add() {}

	@Override
	public void remove() {}

	@Override
	public Instance end() {
		this.tryToRemove();
		return super.end();
	}

	private void tryToRemove() {
		try {
			this.remove();
		} catch (final Exception e) {}
	}

	protected void scheduleSave() {
		EditorContext.asyncExec(new Task(EditorContext.SCHEDULED_SAVER_TASK, 1000) {

			@Override
			public void execute() {
				EditorContext.scheduleSave(AbstractListener.this.eventBus);
			}
		});
	}
}
