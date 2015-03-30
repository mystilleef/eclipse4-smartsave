
package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.events.SyncFilesEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.Task;

final class FileSyncer extends AbstractEventBusInstance {

  final IEditorPart editor = EditorContext.getEditor();
  boolean completionAssistantIsActive;

  public FileSyncer() {
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
  public void syncFiles(@SuppressWarnings("unused") final SyncFilesEvent event) {
    new Task(EditorContext.FILE_SYNCER_TASK, EditorContext
      .getSaveIntervalInMilliSeconds()) {

      @Override
      public boolean shouldSchedule() {
        if (FileSyncer.this.completionAssistantIsActive) return false;
        return EditorContext.taskDoesNotExist(EditorContext.FILE_SYNCER_TASK,
          EditorContext.LISTENER_TASK, EditorContext.SCHEDULED_SAVER_TASK);
      }

      @Override
      public boolean shouldRun() {
        if (FileSyncer.this.completionAssistantIsActive) return false;
        return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK,
          EditorContext.SCHEDULED_SAVER_TASK);
      }

      @Override
      public void execute() {
        // EditorContext.syncFile(FileSyncer.this.editor);
        EventBus.post(new StartSaveScheduleEvent());
      }
    }.begin();
  }
}
