// $codepro.audit.disable methodChainLength
package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import java.util.logging.Level;

import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.ContentAssistantFacade;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

@Log
public final class SaverCompletionListener implements ICompletionListener {

	private boolean isListening;
	private final ISaverCompletionListener handler;
	private final ContentAssistantFacade contentAssistantFacade = SaverCompletionListener.getContentAssistantFacade();
	private final IQuickAssistAssistant quickAssistAssistant = SaverCompletionListener.getQuickAssistAssistant();
	private final EndedRunnable endedRunnable = new EndedRunnable();
	private final StartedRunnable startedRunnable = new StartedRunnable();

	public SaverCompletionListener(final ISaverCompletionListener handler) {
		this.handler = handler;
	}

	public void start() {
		if (this.isListening || (this.contentAssistantFacade == null)) return;
		this.contentAssistantFacade.addCompletionListener(this);
		this.quickAssistAssistant.addCompletionListener(this);
		this.isListening = true;
	}

	public void stop() {
		if (!this.isListening) return;
		this.contentAssistantFacade.removeCompletionListener(this);
		this.quickAssistAssistant.removeCompletionListener(this);
		this.isListening = false;
	}

	@Override
	public String toString() {
		return String.format("SaverCompletionListener [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}

	private static ContentAssistantFacade getContentAssistantFacade() {
		try {
			return EditorContext.getView().getContentAssistantFacade();
		} catch (final NullPointerException e) {
			SaverCompletionListener.log.log(Level.FINEST, "No content assistant found", e);
			return null;
		}
	}

	private static IQuickAssistAssistant getQuickAssistAssistant() {
		try {
			return EditorContext.getView().getQuickAssistAssistant();
		} catch (final NullPointerException e) {
			SaverCompletionListener.log.log(Level.FINEST, "No quick assistant found", e);
			return null;
		}
	}

	@Override
	public void assistSessionEnded(final ContentAssistEvent event) {
		EditorContext.asyncExec(this.endedRunnable);
	}

	@Override
	public void assistSessionStarted(final ContentAssistEvent event) {
		EditorContext.asyncExec(this.startedRunnable);
	}

	@Override
	public void selectionChanged(final ICompletionProposal event, final boolean changed) {}

	@NoArgsConstructor
	private final class EndedRunnable implements Runnable {

		@Override
		public void run() {
			SaverCompletionListener.this.getHandler().assistSessionEnded();
		}
	}

	@NoArgsConstructor
	private final class StartedRunnable implements Runnable {

		@Override
		public void run() {
			SaverCompletionListener.this.getHandler().assistSessionStarted();
		}
	}

	protected ISaverCompletionListener getHandler() {
		return this.handler;
	}
}
