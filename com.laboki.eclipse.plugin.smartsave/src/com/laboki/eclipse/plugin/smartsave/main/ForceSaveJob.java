package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.core.resources.IFile;
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
	protected void
	save() {
		if (!this.editor.isPresent()) return;
		this.editor.get().getSite().getPage().saveEditor(this.editor.get(), false);
	}

	@Override
	public void
	execute(final Optional<IEditorPart> editor) {
		this.editor = editor;
		this.setNewRule(editor);
		this.schedule();
	}

	@Override
	protected void
	setNewRule(final Optional<IEditorPart> editorPart) {
		if (!this.editor.isPresent()) return;
		final Optional<IFile> file = EditorContext.getFile(this.editor);
		if (!file.isPresent()) return;
		this.setRule(file.get());
	}
}
