package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

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

	public void start() {
		if (this.isListening) return;
		this.editorBuffer.addPropertyListener(this);
		this.isListening = true;
	}

	public void stop() {
		if (!this.isListening) return;
		this.editorBuffer.removePropertyListener(this);
		this.isListening = false;
	}

	@Override
	public void propertyChanged(final Object source, final int propID) {
		if (propID == IEditorPart.PROP_DIRTY) ActivePart.asyncExec(this.modifyRunnable);
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

	@Override
	public String toString() {
		return String.format("AutosaveModifyListener [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
