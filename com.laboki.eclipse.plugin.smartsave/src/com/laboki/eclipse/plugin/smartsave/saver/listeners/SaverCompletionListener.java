package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import java.util.logging.Level;

import lombok.extern.java.Log;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.ContentAssistantFacade;
import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.smartsave.Instance;
import com.laboki.eclipse.plugin.smartsave.Task;
import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.EventBus;
import com.laboki.eclipse.plugin.smartsave.saver.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.saver.events.AssistSessionStartedEvent;

@Log
public final class SaverCompletionListener implements Instance, ICompletionListener {

	private final EventBus eventBus;
	private static final Level FINEST = Level.FINEST;
	private final IEditorPart editor = EditorContext.getEditor();
	private final ContentAssistantFacade contentAssistantFacade = this.getContentAssistantFacade();
	private final IQuickAssistAssistant quickAssistAssistant = this.getQuickAssistAssistant();

	public SaverCompletionListener(final EventBus eventbus) {
		this.eventBus = eventbus;
	}

	@Override
	public void assistSessionEnded(final ContentAssistEvent event) {
		EditorContext.asyncExec(new Task("") {

			@Override
			public void execute() {
				SaverCompletionListener.this.eventBus.post(new AssistSessionEndedEvent());
			}
		});
	}

	@Override
	public void assistSessionStarted(final ContentAssistEvent event) {
		EditorContext.asyncExec(new Task("") {

			@Override
			public void execute() {
				SaverCompletionListener.this.eventBus.post(new AssistSessionStartedEvent());
			}
		});
	}

	@Override
	public void selectionChanged(final ICompletionProposal arg0, final boolean arg1) {}

	@Override
	public Instance begin() {
		this.eventBus.register(this);
		this.tryToAdd();
		return this;
	}

	private void tryToAdd() {
		try {
			this.add();
		} catch (final Exception e) {
			SaverCompletionListener.log.log(SaverCompletionListener.FINEST, "failed to add listener");
		}
	}

	private void add() {
		this.contentAssistantFacade.addCompletionListener(this);
		this.quickAssistAssistant.addCompletionListener(this);
	}

	@Override
	public Instance end() {
		this.eventBus.unregister(this);
		this.tryToRemove();
		return this;
	}

	private void tryToRemove() {
		try {
			this.remove();
		} catch (final Exception e) {
			SaverCompletionListener.log.log(SaverCompletionListener.FINEST, "failed to remove listener");
		}
	}

	private void remove() {
		this.contentAssistantFacade.removeCompletionListener(this);
		this.quickAssistAssistant.removeCompletionListener(this);
	}

	private ContentAssistantFacade getContentAssistantFacade() {
		try {
			return EditorContext.getView(this.editor).getContentAssistantFacade();
		} catch (final Exception e) {
			return null;
		}
	}

	private IQuickAssistAssistant getQuickAssistAssistant() {
		try {
			return EditorContext.getView(this.editor).getQuickAssistAssistant();
		} catch (final Exception e) {
			return null;
		}
	}
}
