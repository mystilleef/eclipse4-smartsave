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

  private static final String SAVER_SAVER_TASK = "SAVER_SAVER_TASK";
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
    new AsyncTask(Saver.SAVER_SAVER_TASK, EditorContext.SHORT_DELAY_TIME) {

      @Override
      public boolean shouldSchedule() {
        if (Saver.this.completionAssistantIsActive) return false;
        return super.shouldSchedule() && EditorContext.hasNoSaverTaskJobs();
      }

      @Override
      public boolean shouldRun() {
        if (Saver.this.completionAssistantIsActive) return false;
        return super.shouldRun();
      }

      @Override
      public boolean belongsTo(final Object family) {
        return family.equals(EditorContext.SAVER_TASK_FAMILY);
      }

      @Override
      public void asyncExecute() {
        Saver.this.save();
      }
    }.setTaskRule(EditorContext.SAVER_TASK_RULE).begin();
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
