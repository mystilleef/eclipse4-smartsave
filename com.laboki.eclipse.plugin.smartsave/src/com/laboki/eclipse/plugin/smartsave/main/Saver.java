
package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.StartSaveScheduleEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class Saver extends AbstractEventBusInstance {

  private final IEditorPart editor = EditorContext.getEditor();
  private boolean completionAssistantIsActive;

  public Saver() {
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
  public void save(
    @SuppressWarnings("unused") final StartSaveScheduleEvent event) {
    new AsyncTask(EditorContext.AUTOMATIC_SAVER_TASK,
      EditorContext.SHORT_DELAY_TIME) {

      @Override
      public boolean shouldSchedule() {
        if (Saver.this.completionAssistantIsActive) return false;
        return EditorContext.taskDoesNotExist(
          EditorContext.AUTOMATIC_SAVER_TASK, EditorContext.LISTENER_TASK,
          EditorContext.SCHEDULED_SAVER_TASK, EditorContext.FILE_SYNCER_TASK);
      }

      @Override
      public boolean shouldRun() {
        if (Saver.this.completionAssistantIsActive) return false;
        return EditorContext.taskDoesNotExist(EditorContext.LISTENER_TASK,
          EditorContext.SCHEDULED_SAVER_TASK, EditorContext.FILE_SYNCER_TASK);
      }

      @Override
      public void asyncExecute() {
        Saver.this.save();
      }
    }.begin();
  }

  @Override
  public Instance end() {
    this.save();
    return super.end();
  }

  private void save() {
    EditorContext.tryToSave(this.editor);
  }
}
