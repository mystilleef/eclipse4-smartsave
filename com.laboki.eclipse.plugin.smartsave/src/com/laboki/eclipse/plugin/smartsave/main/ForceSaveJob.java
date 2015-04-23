package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorPart;

public final class ForceSaveJob extends SaveJob {

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
	protected void
	save() {
		EditorContext.WORKBENCH.saveAllEditors(false);
	}

	@Override
	public void
	execute(final IEditorPart editorPart) {
		this.schedule(EditorContext.SHORT_DELAY);
	}
}
