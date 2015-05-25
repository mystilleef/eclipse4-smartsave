package com.laboki.eclipse.plugin.smartsave.main;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.BaseTask;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class Scheduler extends EventBusInstance {

	public static final int DELAY = 10;
	public static final TaskMutexRule RULE = new TaskMutexRule();
	public static final String FAMILY = "SmartSaveSchedulerTaskFamily";
	boolean canSchedule = true;

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final AssistSessionStartedEvent event) {
		this.canSchedule = false;
		Scheduler.cancelTasks();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final AssistSessionEndedEvent event) {
		this.canSchedule = true;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final ScheduleSaveEvent event) {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				if (!Scheduler.this.canSchedule) return false;
				return BaseTask.noTaskFamilyExists(Scheduler.FAMILY);
			}

			@Override
			public void
			execute() {
				Scheduler.cancelTasks();
				EventBus.post(new StartSaveScheduleEvent());
			}
		}.setDelay(Scheduler.DELAY)
			.setFamily(Scheduler.FAMILY)
			.setRule(Scheduler.RULE)
			.start();
	}

	private static void
	cancelTasks() {
		EditorContext.cancelSaverTasks();
	}
}
