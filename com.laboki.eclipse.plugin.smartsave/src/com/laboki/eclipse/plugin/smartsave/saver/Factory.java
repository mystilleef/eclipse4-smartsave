package com.laboki.eclipse.plugin.smartsave.saver;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

@ToString
public final class Factory implements Runnable {

	private final IPartService partService;
	private final PartListener partListener = new PartListener();
	@Getter private final List<IEditorPart> editorParts = new ArrayList<>();

	public Factory(final IPartService partService) {
		this.partService = partService;
		this.partService.addPartListener(this.partListener);
	}

	public void enableAutomaticSaverFor(final IWorkbenchPart part) {
		if (this.isInvalidPart(part)) return;
		this.editorParts.add((IEditorPart) part);
		EditorContext.asyncExec(new AutomaticSaver());
	}

	private boolean isInvalidPart(final IWorkbenchPart part) {
		return (part == null) || this.getEditorParts().contains(part) || !(part instanceof IEditorPart);
	}

	@Override
	public void run() {
		EditorContext.instance();
		this.enableAutomaticSaverFor(this.partService.getActivePart());
	}

	private final class PartListener implements IPartListener {

		public PartListener() {}

		@Override
		public void partActivated(final IWorkbenchPart part) {
			Factory.this.enableAutomaticSaverFor(part);
		}

		@Override
		public void partClosed(final IWorkbenchPart part) {
			if (Factory.this.getEditorParts().contains(part)) Factory.this.getEditorParts().remove(part);
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {}

		@Override
		public void partDeactivated(final IWorkbenchPart part) {}

		@Override
		public void partOpened(final IWorkbenchPart part) {}
	}
}
