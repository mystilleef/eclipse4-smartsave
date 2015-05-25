package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;

public final class PartMonitor implements Instance {

	private static final Optional<IPartService> SERVICE =
		EditorContext.getPartService();
	private static final PartListener LISTENER = new PartListener();

	@Override
	public Instance
	start() {
		if (!PartMonitor.SERVICE.isPresent()) return this;
		PartMonitor.SERVICE.get().addPartListener(PartMonitor.LISTENER);
		return this;
	}

	@Override
	public Instance
	stop() {
		if (!PartMonitor.SERVICE.isPresent()) return this;
		PartMonitor.SERVICE.get().removePartListener(PartMonitor.LISTENER);
		return this;
	}

	private static final class PartListener implements IPartListener {

		private final Instance partServices = new PartServices();

		public PartListener() {
			this.initPartServices();
		}

		private void
		initPartServices() {
			final Optional<IEditorPart> editor = EditorContext.getEditor();
			if (editor.isPresent()) this.startPartServices(editor.get());
		}

		@Override
		public void
		partActivated(final IWorkbenchPart part) {
			this.startPartServices(part);
		}

		private void
		startPartServices(final IWorkbenchPart part) {
			if (EditorContext.isEditorPart(part)) this.partServices.start();
		}

		@Override
		public void
		partClosed(final IWorkbenchPart part) {}

		@Override
		public void
		partBroughtToTop(final IWorkbenchPart part) {}

		@Override
		public void
		partDeactivated(final IWorkbenchPart part) {
			this.stopPartServices(part);
		}

		private void
		stopPartServices(final IWorkbenchPart part) {
			if (EditorContext.isEditorPart(part)) this.partServices.stop();
		}

		@Override
		public void
		partOpened(final IWorkbenchPart part) {}
	}
}
