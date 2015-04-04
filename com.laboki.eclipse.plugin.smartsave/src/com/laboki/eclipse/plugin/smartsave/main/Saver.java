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
  boolean completionAssistantIsActive;
  private final IEditorPart editor = EditorContext.getEditor();

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
        return EditorContext.canScheduleSave();
      }

      @Override
      public void execute() {
        Saver.this.save();
      }
    }.setName(Saver.SAVER_TASK)
        .setFamily(EditorContext.SAVER_TASK_FAMILY)
        .setDelay(EditorContext.SHORT_DELAY)
        .setRule(EditorContext.SAVER_TASK_RULE).begin();
  }

  @Override
  public Instance end() {
    this.save();
    return super.end();
  }

  void save() {
    EditorContext.save(this.editor);
  }
}
