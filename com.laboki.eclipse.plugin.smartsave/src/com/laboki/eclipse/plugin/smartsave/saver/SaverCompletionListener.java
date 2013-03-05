package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ContentAssistantFacade;

final class SaverCompletionListener implements ICompletionListener {

	private boolean isListening;
	private final ISaverCompletionListener handler;
	private final ContentAssistantFacade contentAssistantFacade = EditorContext.getView().getContentAssistantFacade();
	private final EndedRunnable endedRunnable = new EndedRunnable();
	private final StartedRunnable startedRunnable = new StartedRunnable();

	public SaverCompletionListener(final ISaverCompletionListener handler) {
		this.handler = handler;
	}

	public void start() {
		if (this.isListening) return;
		this.contentAssistantFacade.addCompletionListener(this);
		this.isListening = true;
	}

	public void stop() {
		if (!this.isListening) return;
		this.contentAssistantFacade.removeCompletionListener(this);
		this.isListening = false;
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

	private final class EndedRunnable implements Runnable {

		public EndedRunnable() {}

		@Override
		public void run() {
			SaverCompletionListener.this.getHandler().assistSessionEnded();
		}
	}

	private final class StartedRunnable implements Runnable {

		public StartedRunnable() {}

		@Override
		public void run() {
			SaverCompletionListener.this.getHandler().assistSessionStarted();
		}
	}

	protected ISaverCompletionListener getHandler() {
		return this.handler;
	}
}
