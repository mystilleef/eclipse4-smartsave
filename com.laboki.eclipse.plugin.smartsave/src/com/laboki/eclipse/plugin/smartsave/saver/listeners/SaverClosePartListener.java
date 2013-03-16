package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import lombok.Getter;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

public final class SaverClosePartListener extends AbstractSaverListener implements IPartListener {

	@Getter private final ISaverClosePartListenerHandler handler;
	private final IEditorPart editor = EditorContext.getEditor();
	private final IPartService partService = (IPartService) this.editor.getSite().getService(IPartService.class);
	private final Runnable closePartRunnable = new ClosePartRunnable();

	public SaverClosePartListener(final ISaverClosePartListenerHandler handler) {
		this.handler = handler;
	}

	@Override
	public void add() {
		this.partService.addPartListener(this);
	}

	@Override
	public void remove() {
		this.partService.removePartListener(this);
	}

	@Override
	public void partClosed(final IWorkbenchPart part) {
		if (part != this.editor) return;
		EditorContext.asyncExec(this.closePartRunnable);
	}

	@Override
	public void partActivated(final IWorkbenchPart part) {}

	@Override
	public void partBroughtToTop(final IWorkbenchPart part) {}

	@Override
	public void partDeactivated(final IWorkbenchPart part) {}

	@Override
	public void partOpened(final IWorkbenchPart part) {}

	private final class ClosePartRunnable implements Runnable {

		public ClosePartRunnable() {}

		@Override
		public void run() {
			SaverClosePartListener.this.getHandler().close();
		}
	}
}
