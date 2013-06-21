package com.laboki.eclipse.plugin.smartsave.listeners.abstraction;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public abstract class AbstractListener extends AbstractEventBusInstance implements IListener {

	private static final Logger LOGGER = Logger.getLogger(AbstractListener.class.getName());

	public AbstractListener(final EventBus eventbus) {
		super(eventbus);
	}

	@Subscribe
	public void addListener(@SuppressWarnings("unused") final EnableSaveListenersEvent event) {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				AbstractListener.this.tryToAdd();
			}
		}.begin();
	}

	private void tryToAdd() {
		try {
			this.add();
		} catch (final Exception e) {
			AbstractListener.LOGGER.log(Level.WARNING, "failed to add listener for object", e);
		}
	}

	@Subscribe
	public void removeListener(@SuppressWarnings("unused") final DisableSaveListenersEvent event) {
		new AsyncTask() {

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
		} catch (final Exception e) {
			AbstractListener.LOGGER.log(Level.WARNING, "failed to remove listener for object", e);
		}
	}

	protected void scheduleSave() {
		new Task(EditorContext.LISTENER_TASK, 1000) {

			@Override
			public boolean shouldSchedule() {
				return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
			}

			@Override
			public void execute() {
				EditorContext.scheduleSave(AbstractListener.this.eventBus);
			}
		}.begin();
	}
}
