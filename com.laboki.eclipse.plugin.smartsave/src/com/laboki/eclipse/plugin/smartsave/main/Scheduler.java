
package com.laboki.eclipse.plugin.smartsave.main;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.ScheduleSaveEvent;
import com.laboki.eclipse.plugin.smartsave.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class Scheduler extends AbstractEventBusInstance {

  public Scheduler() {
    super();
  }

  @Subscribe
  @AllowConcurrentEvents
  public static void scheduleSave(
    @SuppressWarnings("unused") final ScheduleSaveEvent event) {
    new Task(EditorContext.SCHEDULED_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

      @Override
      public boolean shouldSchedule() {
        return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
      }

      @Override
      public boolean shouldRun() {
        return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK);
      }

      @Override
      public void execute() {
        Scheduler.cancelAllJobs();
        EventBus.post(new SyncFilesEvent());
      }
    }.begin();
  }

  @Subscribe
  public static void scheduleSave(
    @SuppressWarnings("unused") final EnableSaveListenersEvent event) {
    new Task(EditorContext.SCHEDULER_ENABLE_SAVE_LISTENERS_TASK) {

      @Override
      public void execute() {
        EditorContext.scheduleSave(EditorContext.SHORT_DELAY_TIME);
      }
    }.begin();
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
    EditorContext.cancelAllJobs();
  }
}
