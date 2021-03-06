package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.ContentAssistantFacade;
import org.eclipse.jface.text.source.SourceViewer;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;

public final class CompletionListener extends EventBusInstance
	implements
		ICompletionListener {

	private final Optional<ContentAssistantFacade> contentAssistant =
		CompletionListener.getContentAssistant();
	private final Optional<IQuickAssistAssistant> quickAssistant =
		CompletionListener.getQuickAssistant();

	@Override
	public void
	assistSessionEnded(final ContentAssistEvent event) {
		EventBus.post(new AssistSessionEndedEvent());
	}

	@Override
	public void
	assistSessionStarted(final ContentAssistEvent event) {
		EventBus.post(new AssistSessionStartedEvent());
	}

	@Override
	public void
	selectionChanged(final ICompletionProposal arg0, final boolean arg1) {}

	@Override
	public Instance
	start() {
		this.add();
		return super.start();
	}

	private void
	add() {
		if (this.contentAssistant.isPresent()) this.contentAssistant.get()
			.addCompletionListener(this);
		if (this.quickAssistant.isPresent()) this.quickAssistant.get()
			.addCompletionListener(this);
	}

	@Override
	public Instance
	stop() {
		this.remove();
		return super.stop();
	}

	private void
	remove() {
		if (this.contentAssistant.isPresent()) this.contentAssistant.get()
			.removeCompletionListener(this);
		if (this.quickAssistant.isPresent()) this.quickAssistant.get()
			.removeCompletionListener(this);
	}

	private static Optional<ContentAssistantFacade>
	getContentAssistant() {
		final Optional<SourceViewer> view = CompletionListener.getView();
		if (!view.isPresent()) return Optional.absent();
		final ContentAssistantFacade facade =
			view.get().getContentAssistantFacade();
		return Optional.fromNullable(facade);
	}

	private static Optional<IQuickAssistAssistant>
	getQuickAssistant() {
		final Optional<SourceViewer> view = CompletionListener.getView();
		if (!view.isPresent()) return Optional.absent();
		return Optional.fromNullable(view.get().getQuickAssistAssistant());
	}

	private static Optional<SourceViewer>
	getView() {
		return EditorContext.getView(EditorContext.getEditor());
	}
}
