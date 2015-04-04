package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.FocusSaveIntervalDialogSpinnerEvent;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

final class SaveIntervalDialogSpinner extends AbstractEventBusInstance {

  private static final String FOCUS_TASK = "save dialog interval focuc task";
  private static final String SELECTION_TASK =
      "save interval dialog update selection task";
  static final TaskMutexRule RULE = new TaskMutexRule();
  private static final int TEXT_LIMIT = 3;
  private static final int SPINNER_PAGE_INCREMENTS = 10;
  private static final int SPINNER_INCREMENTS = 1;
  private static final int SPINNER_DIGITS = 0;
  private static final int SPINNER_MAXIMUM = 600;
  private static final int SPINNER_MINIMUM = 1;
  private final ModifyListener modifyListener = new SpinnerModifyListener();
  private final SpinnerTraverseListener traverseListener =
      new SpinnerTraverseListener();
  final Spinner spinner;

  public SaveIntervalDialogSpinner(final Composite composite) {
    super();
    this.spinner = new Spinner(composite, SWT.BORDER | SWT.RIGHT);
  }

  @Override
  public Instance begin() {
    this.updateProperties();
    this.startListening();
    return super.begin();
  }

  private void updateProperties() {
    this.spinner.setTextLimit(SaveIntervalDialogSpinner.TEXT_LIMIT);
    this.spinner.setValues(Store.getSaveIntervalInSeconds(),
        SaveIntervalDialogSpinner.SPINNER_MINIMUM,
        SaveIntervalDialogSpinner.SPINNER_MAXIMUM,
        SaveIntervalDialogSpinner.SPINNER_DIGITS,
        SaveIntervalDialogSpinner.SPINNER_INCREMENTS,
        SaveIntervalDialogSpinner.SPINNER_PAGE_INCREMENTS);
    this.focus();
  }

  public void startListening() {
    this.spinner.addModifyListener(this.modifyListener);
    this.spinner.addTraverseListener(this.traverseListener);
  }

  @Subscribe
  public void preferencesChanged(
      @SuppressWarnings("unused") final PreferenceStoreChangeEvent event) {
    new AsyncTask() {

      @Override
      public void execute() {
        SaveIntervalDialogSpinner.this.updateSelection();
      }
    }.setName(SaveIntervalDialogSpinner.SELECTION_TASK)
        .setRule(SaveIntervalDialogSpinner.RULE)
        .begin();
  }

  protected void updateSelection() {
    if ((this.spinner == null) || this.spinner.isDisposed()) return;
    if (this.spinner.getSelection() == Store.getSaveIntervalInSeconds()) return;
    this.spinner.removeModifyListener(this.modifyListener);
    this.spinner.setSelection(Store.getSaveIntervalInSeconds());
    this.spinner.addModifyListener(this.modifyListener);
  }

  @Subscribe
  public
      void
      focusSpinner(
          @SuppressWarnings("unused") final FocusSaveIntervalDialogSpinnerEvent event) {
    new AsyncTask() {

      @Override
      public void execute() {
        SaveIntervalDialogSpinner.this.focus();
      }
    }.setName(SaveIntervalDialogSpinner.FOCUS_TASK)
        .setFamily("FOCUS_SAVE_DIALOG_TASK_NAME")
        .setRule(SaveIntervalDialogSpinner.RULE)
        .begin();
  }

  void focus() {
    if ((this.spinner == null) || this.spinner.isDisposed()) return;
    this.spinner.setFocus();
    this.spinner.forceFocus();
  }

  @Override
  public Instance end() {
    this.spinner.dispose();
    return super.end();
  }

  private final class SpinnerModifyListener implements ModifyListener {

    public SpinnerModifyListener() {}

    @Override
    public void modifyText(final ModifyEvent event) {
      new AsyncTask() {

        @Override
        public void execute() {
          Store.setSaveIntervalInSeconds(SaveIntervalDialogSpinner.this
              .spinner.getSelection());
        }
      }.setRule(SaveIntervalDialogSpinner.RULE).begin();
    }
  }

  private final class SpinnerTraverseListener
      implements TraverseListener {

    public SpinnerTraverseListener() {}

    @Override
    public void keyTraversed(final TraverseEvent event) {
      if (event.detail == SWT.TRAVERSE_RETURN) new AsyncTask() {

        @Override
        public void execute() {
          SaveIntervalDialogSpinner.this.spinner.getShell().close();
        }
      }.begin();
    }
  }
}
