package com.laboki.eclipse.plugin.smartsave.contexts;

import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.ForceSaver;

public final class FinalSaver extends EventBusInstance {

	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	@Override
	public Instance
	stop() {
		if (this.editor.isPresent()) this.forceSave();
		return super.stop();
	}

	private void
	forceSave() {
		if (this.canSave()) this.save();
	}

	private boolean
	canSave() {
		return EditorContext.canSaveAutomatically(this.editor);
	}

	private void
	save() {
		new ForceSaver(this.editor.get()).save();
	}
}
