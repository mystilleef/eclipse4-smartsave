package com.laboki.eclipse.plugin.smartsave.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.ContentAssistantFacade;
import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class CompletionListener extends AbstractEventBusInstance
implements ICompletionListener {

  private final IEditorPart editor = EditorContext.getEditor();
  private final ContentAssistantFacade contentAssistantFacade = this
      .getContentAssistantFacade();
  private final IQuickAssistAssistant quickAssistAssistant = this
      .getQuickAssistAssistant();
  private static final Logger LOGGER = Logger
      .getLogger(CompletionListener.class.getName());

  public CompletionListener() {
    super();
  }

  @Override
  public void assistSessionEnded(final ContentAssistEvent event) {
    new Task() {

      @Override
      public void execute() {
        EventBus.post(new AssistSessionEndedEvent());
      }
    }.begin();
  }

  @Override
  public void assistSessionStarted(final ContentAssistEvent event) {
    new Task() {

      @Override
      public void execute() {
        EventBus.post(new AssistSessionStartedEvent());
      }
    }.begin();
  }

  @Override
  public void selectionChanged(final ICompletionProposal arg0,
    final boolean arg1) {}

  @Override
  public Instance begin() {
    this.tryToAdd();
    return super.begin();
  }

  private void tryToAdd() {
    try {
      this.add();
    }
    catch (final Exception e) {
      CompletionListener.LOGGER.log(Level.OFF, e.getMessage(), e);
    }
  }

  private void add() {
    if (this.contentAssistantFacade != null) this.contentAssistantFacade
    .addCompletionListener(this);
    if (this.quickAssistAssistant != null) this.quickAssistAssistant
    .addCompletionListener(this);
  }

  @Override
  public Instance end() {
    this.tryToRemove();
    return super.end();
  }

  private void tryToRemove() {
    try {
      this.remove();
    }
    catch (final Exception e) {
      CompletionListener.LOGGER.log(Level.OFF, e.getMessage(), e);
    }
  }

  private void remove() {
    if (this.contentAssistantFacade != null) this.contentAssistantFacade
      .removeCompletionListener(this);
    if (this.quickAssistAssistant != null) this.quickAssistAssistant
    .removeCompletionListener(this);
  }

  private ContentAssistantFacade getContentAssistantFacade() {
    try {
      return EditorContext.getView(this.editor).getContentAssistantFacade();
    }
    catch (final Exception e) {
      return null;
    }
  }

  private IQuickAssistAssistant getQuickAssistAssistant() {
    try {
      return EditorContext.getView(this.editor).getQuickAssistAssistant();
    }
    catch (final Exception e) {
      return null;
    }
  }
}
