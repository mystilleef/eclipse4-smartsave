package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;

public final class PartMonitor implements Instance, IPartListener {

	private final Instance partServices = new PartServices();
	private final Optional<IPartService> service =
		EditorContext.getPartService();

	public PartMonitor() {
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
	partDeactivated(final IWorkbenchPart part) {
		this.stopPartServices(part);
	}

	private void
	stopPartServices(final IWorkbenchPart part) {
		if (EditorContext.isEditorPart(part)) this.partServices.stop();
	}

	@Override
	public void
	partClosed(final IWorkbenchPart part) {}

	@Override
	public void
	partBroughtToTop(final IWorkbenchPart part) {}

	@Override
	public void
	partOpened(final IWorkbenchPart part) {}

	@Override
	public Instance
	start() {
		if (!this.service.isPresent()) return this;
		this.service.get().addPartListener(this);
		return this;
	}

	@Override
	public Instance
	stop() {
		if (!this.service.isPresent()) return this;
		this.service.get().removePartListener(this);
		return this;
	}
}
