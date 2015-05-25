package com.laboki.eclipse.plugin.smartsave.listeners.abstraction;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.BaseTask;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public abstract class BaseListener extends AbstractEventBusInstance
	implements
		IListener {

	public static final String FAMILY = "SmartSaveBaseListenerTaskFamily";
	private static final ISchedulingRule RULE = new TaskMutexRule();
	private static final String TASK = "ABSTRACT_LISTENER_SAVER_TASK";
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

	@Override
	public abstract void
	add();

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

	@Override
	public abstract void
	remove();

	protected static final void
	scheduleSave() {
		EditorContext.cancelSaverTasks();
		BaseListener.scheduleTask();
	}

	protected static void
	scheduleTask() {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				return BaseTask.noTaskFamilyExists(BaseTask.FAMILY);
			}

			@Override
			public void
			execute() {
				EventBus.post(new ScheduleSaveEvent());
			}
		}.setName(BaseListener.TASK)
			.setFamily(BaseListener.FAMILY)
			.setDelay(EditorContext.getSaveIntervalInMilliSeconds())
			.setRule(BaseListener.RULE)
			.start();
	}
}
