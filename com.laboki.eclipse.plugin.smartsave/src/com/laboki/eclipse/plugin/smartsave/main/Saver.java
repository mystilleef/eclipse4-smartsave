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

  private static final String SAVER_TASK = "SAVER_SAVER_TASK";
  private final IEditorPart editor = EditorContext.getEditor();
  boolean completionAssistantIsActive;

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
    new AsyncTask() {

      @Override
      public boolean shouldSchedule() {
        if (Saver.this.completionAssistantIsActive) return false;
        return EditorContext.hasNoSaverTaskJobs();
      }

      @Override
      public void execute() {
        Saver.this.save();
      }
    }.setFamily(EditorContext.SAVER_TASK_FAMILY).setName(Saver.SAVER_TASK)
      .setDelay(EditorContext.SHORT_DELAY_TIME).setRule(
        EditorContext.SAVER_TASK_RULE).begin();
  }

  @Override
  public Instance end() {
    this.save();
    return super.end();
  }

  void save() {
    EditorContext.tryToSave(this.editor);
  }
}
