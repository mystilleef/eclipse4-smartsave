package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.ContentAssistantFacade;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class CompletionListener extends AbstractEventBusInstance
	implements
		ICompletionListener {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();
	private final Optional<ContentAssistantFacade> contentAssistant =
		this.getContentAssistant();
	private final Optional<IQuickAssistAssistant> quickAssistant =
		this.getQuickAssistant();

	public CompletionListener() {
		super();
	}

	@Override
	public void
	assistSessionEnded(final ContentAssistEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				EventBus.post(new AssistSessionEndedEvent());
			}
		}.start();
	}

	@Override
	public void
	assistSessionStarted(final ContentAssistEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				EventBus.post(new AssistSessionStartedEvent());
			}
		}.start();
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

	private Optional<ContentAssistantFacade>
	getContentAssistant() {
		if (!this.editor.isPresent()) return Optional.absent();
		final Optional<SourceViewer> view =
			EditorContext.getView(this.editor.get());
		if (!view.isPresent()) return Optional.absent();
		return Optional.fromNullable(view.get().getContentAssistantFacade());
	}

	private Optional<IQuickAssistAssistant>
	getQuickAssistant() {
		if (!this.editor.isPresent()) return Optional.absent();
		final Optional<SourceViewer> view =
			EditorContext.getView(this.editor.get());
		if (!view.isPresent()) return Optional.absent();
		return Optional.fromNullable(view.get().getQuickAssistAssistant());
	}
}
