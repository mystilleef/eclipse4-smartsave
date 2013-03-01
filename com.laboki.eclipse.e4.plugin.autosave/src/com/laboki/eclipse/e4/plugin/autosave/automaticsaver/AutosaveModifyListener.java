package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

final class AutosaveModifyListener implements IPropertyListener {

	private boolean isListening;
	private final IAutosaveModifyListenerHandler handler;
	private final ModifyRunnable modifyRunnable = new ModifyRunnable();
	private final IEditorPart editorBuffer = ActivePart.getEditor();

	public AutosaveModifyListener(final IAutosaveModifyListenerHandler handler) {
		this.handler = handler;
	}

	void start() {
		if (this.isListening) return;
		this.editorBuffer.addPropertyListener(this);
		this.isListening = true;
	}

	void stop() {
		if (!this.isListening) return;
		this.editorBuffer.removePropertyListener(this);
		this.isListening = false;
	}

	@Override
	public void propertyChanged(final Object source, final int propID) {
		if (propID == IEditorPart.PROP_DIRTY) Display.getDefault().asyncExec(this.modifyRunnable);
	}

	public IAutosaveModifyListenerHandler getHandler() {
		return this.handler;
	}

	private final class ModifyRunnable implements Runnable {

		public ModifyRunnable() {}

		@Override
		public void run() {
			AutosaveModifyListener.this.getHandler().modify();
		}
	}
}
