package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;

public final class ForceSaveJob extends SaveJob {

	private Optional<IEditorPart> editor;

	public ForceSaveJob() {
		this.setName("");
		this.setRule(ResourcesPlugin.getWorkspace().getRoot());
	}

	@Override
	public boolean
	belongsTo(final Object family) {
		return false;
	}

	@Override
	public boolean
	shouldSchedule() {
		return true;
	}

	@Override
	public boolean
	shouldRun() {
		return true;
	}

	@Override
	protected void
	save() {
		if (!this.editor.isPresent()) return;
		this.editor.get().getSite().getPage().saveEditor(this.editor.get(), false);
	}

	@Override
	public void
	execute(final Optional<IEditorPart> editor) {
		this.editor = editor;
		this.schedule();
	}
}
