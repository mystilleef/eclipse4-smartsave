
package com.laboki.eclipse.plugin.smartsave.main;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class Scheduler extends AbstractEventBusInstance {

  private static final String SAVER_TASK = "SCHEDULER_SAVER_TASK";
  boolean completionAssistantIsActive;

  public Scheduler() {
    super();
  }

  @Subscribe
  public void save(
    @SuppressWarnings("unused") final AssistSessionStartedEvent event) {
    this.completionAssistantIsActive = true;
  }

  @Subscribe
  public void save(
    @SuppressWarnings("unused") final AssistSessionEndedEvent event) {
    this.completionAssistantIsActive = false;
  }

  @Subscribe
  @AllowConcurrentEvents
  public void scheduleSave(
    @SuppressWarnings("unused") final ScheduleSaveEvent event) {
    new Task(Scheduler.SAVER_TASK, EditorContext
      .getSaveIntervalInMilliSeconds()) {

      @Override
      public boolean shouldSchedule() {
        if (Scheduler.this.completionAssistantIsActive) return false;
        return super.shouldSchedule() && EditorContext.hasNoSaverTaskJobs();
      }

      @Override
      public boolean shouldRun() {
        if (Scheduler.this.completionAssistantIsActive) return false;
        return super.shouldRun();
      }

      @Override
      public boolean belongsTo(final Object family) {
        return family.equals(EditorContext.SAVER_TASK_FAMILY);
      }

      @Override
      public void execute() {
        Scheduler.cancelAllJobs();
        EventBus.post(new StartSaveScheduleEvent());
      }
    }.setTaskRule(EditorContext.SAVER_TASK_RULE).begin();
  }

  @Subscribe
  public static void scheduleSave(
    @SuppressWarnings("unused") final EnableSaveListenersEvent event) {
    new Task(Scheduler.SAVER_TASK) {

      @Override
      public void execute() {
        EditorContext.scheduleSave(EditorContext.SHORT_DELAY_TIME);
      }

      @Override
      public boolean belongsTo(final Object family) {
        return family.equals(EditorContext.SAVER_TASK_FAMILY);
      }
    }.setTaskRule(EditorContext.SAVER_TASK_RULE).begin();
  }

  @Subscribe
  public static void cancelSaveJobs(
    @SuppressWarnings("unused") final DisableSaveListenersEvent event) {
    Scheduler.cancelAllJobs();
  }

  @Subscribe
  public static void cancelSaveJobs(
    @SuppressWarnings("unused") final AssistSessionStartedEvent event) {
    Scheduler.cancelAllJobs();
  }

  static void cancelAllJobs() {
    EditorContext.cancelSaverTaskJobs();
  }
}
