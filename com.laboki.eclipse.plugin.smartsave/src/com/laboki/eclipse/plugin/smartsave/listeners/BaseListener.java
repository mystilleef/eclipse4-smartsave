package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.BaseTask;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public abstract class BaseListener extends EventBusInstance {

	public static final String FAMILY = "+SmartSaveBaseListenerTaskFamily+";
	private static final ISchedulingRule RULE = new TaskMutexRule();

	@Override
	public Instance
	start() {
		this.add();
		return super.start();
	}

	protected abstract void
	add();

	@Override
	public final Instance
	stop() {
		this.remove();
		return super.stop();
	}

	protected abstract void
	remove();

	protected static final void
	scheduleSave() {
		EditorContext.cancelSaverTasks();
		BaseListener.scheduleTask();
	}

	private static void
	scheduleTask() {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				return BaseTask.noTaskFamilyExists(BaseListener.FAMILY);
			}

			@Override
			public void
			execute() {
				EventBus.post(new ScheduleSaveEvent());
			}
		}.setDelay(EditorContext.getSaveIntervalInMilliSeconds())
			.setFamily(BaseListener.FAMILY)
			.setRule(BaseListener.RULE)
			.start();
	}
}
