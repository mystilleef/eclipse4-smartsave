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

public abstract class BaseListener extends AbstractEventBusInstance
	implements
		IListener {

	private static final ISchedulingRule RULE =
		MultiRule.combine(EditorContext.SAVER_TASK_RULE, new TaskMutexRule());
	private static final String SAVER_TASK = "ABSTRACT_LISTENER_SAVER_TASK";
	private static final int ONE_SECOND_DELAY = 1000;
	private static final Logger LOGGER =
		Logger.getLogger(BaseListener.class.getName());

	@Override
	public Instance
	start() {
		try {
			this.add();
		}
		catch (final Exception e) {
			BaseListener.LOGGER.log(Level.FINEST, e.getMessage(), e);
		}
		return super.start();
	}

	@Subscribe
	public final void
	eventHandler(final EnableSaveListenersEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				BaseListener.this.add();
			}
		}.start();
	}

	@Subscribe
	public final void
	eventHandler(final DisableSaveListenersEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				BaseListener.this.remove();
			}
		}.start();
	}

	@Override
	public abstract void
	add();

	@Override
	public abstract void
	remove();

	@Override
	public final Instance
	stop() {
		try {
			this.remove();
		}
		catch (final Exception e) {
			BaseListener.LOGGER.log(Level.FINEST, e.getMessage(), e);
		}
		return super.stop();
	}

	protected static final void
	scheduleSave() {
		EditorContext.cancelAllSaverTasks();
		BaseListener.scheduleTask();
	}

	private static void
	scheduleTask() {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				return EditorContext.canScheduleSave();
			}

			@Override
			public void
			execute() {
				EditorContext.scheduleSave();
			}
		}.setName(BaseListener.SAVER_TASK)
			.setFamily(EditorContext.SAVER_TASK_FAMILY)
			.setDelay(BaseListener.ONE_SECOND_DELAY)
			.setRule(BaseListener.RULE)
			.start();
	}
}
