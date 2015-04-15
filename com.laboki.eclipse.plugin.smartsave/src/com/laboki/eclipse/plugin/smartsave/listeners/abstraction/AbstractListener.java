package com.laboki.eclipse.plugin.smartsave.listeners.abstraction;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public abstract class AbstractListener extends AbstractEventBusInstance
implements
IListener {

	private static final ISchedulingRule RULE =
	MultiRule.combine(EditorContext.SAVER_TASK_RULE, new TaskMutexRule());
	private static final String SAVER_TASK = "ABSTRACT_LISTENER_SAVER_TASK";
	private static final int ONE_SECOND_DELAY = 1000;
	private static final Logger LOGGER =
		Logger.getLogger(AbstractListener.class.getName());

	public AbstractListener() {
		super();
	}

	@Subscribe
	public final void addListener(final EnableSaveListenersEvent event) {
		new AsyncTask() {

			@Override
			public void execute() {
				AbstractListener.this.tryToAdd();
			}
		}.start();
	}

	void tryToAdd() {
		try {
			this.add();
		}
		catch (final Exception e) {
			AbstractListener.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Subscribe
	public final void removeListener(final DisableSaveListenersEvent event) {
		new AsyncTask() {

			@Override
			public void execute() {
				AbstractListener.this.tryToRemove();
			}
		}.start();
	}

	@Override
	public void add() {}

	@Override
	public void remove() {}

	@Override
	public final Instance stop() {
		this.tryToRemove();
		return super.stop();
	}

	void tryToRemove() {
		try {
			this.remove();
		}
		catch (final Exception e) {
			AbstractListener.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	protected static final void scheduleSave() {
		EditorContext.cancelAllSaverTasks();
		AbstractListener.scheduleTask();
	}

	private static void scheduleTask() {
		new Task() {

			@Override
			public boolean shouldSchedule() {
				return EditorContext.canScheduleSave();
			}

			@Override
			public void execute() {
				EditorContext.scheduleSave();
			}
		}.setName(AbstractListener.SAVER_TASK)
		.setFamily(EditorContext.SAVER_TASK_FAMILY)
		.setDelay(AbstractListener.ONE_SECOND_DELAY)
		.setRule(AbstractListener.RULE)
		.start();
	}
}
