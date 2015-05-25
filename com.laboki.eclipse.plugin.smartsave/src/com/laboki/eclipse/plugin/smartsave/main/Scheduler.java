package com.laboki.eclipse.plugin.smartsave.main;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
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
	boolean completionAssistantIsActive;

	@Subscribe
	public void
	eventHandler(final AssistSessionStartedEvent event) {
		this.completionAssistantIsActive = true;
		Scheduler.cancelTasks();
	}

	@Subscribe
	public void
	eventHandler(final AssistSessionEndedEvent event) {
		this.completionAssistantIsActive = false;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final ScheduleSaveEvent event) {
		new Task() {

			@Override
			public boolean
			shouldSchedule() {
				if (Scheduler.this.completionAssistantIsActive) return false;
				return BaseTask.noTaskFamilyExists(BaseTask.FAMILY);
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

	@Subscribe
	public static void
	eventHandler(final EnableSaveListenersEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				EventBus.post(new ScheduleSaveEvent());
			}
		}.setDelay(Scheduler.DELAY)
			.setFamily(Scheduler.FAMILY)
			.setRule(Scheduler.RULE)
			.start();
	}

	@Subscribe
	public static void
	eventHandler(final DisableSaveListenersEvent event) {
		Scheduler.cancelTasks();
	}

	static void
	cancelTasks() {
		EditorContext.cancelSaverTasks();
	}
}
