package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

final class SaverModifyListener implements IPropertyListener {

	private boolean isListening;
	private final ISaverModifyListenerHandler handler;
	private final ModifyRunnable modifyRunnable = new ModifyRunnable();
	private final IEditorPart editorBuffer = EditorContext.getEditor();

	public SaverModifyListener(final ISaverModifyListenerHandler handler) {
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
		if (propID == IEditorPart.PROP_DIRTY) EditorContext.asyncExec(this.modifyRunnable);
	}

	public ISaverModifyListenerHandler getHandler() {
		return this.handler;
	}

	private final class ModifyRunnable implements Runnable {

		public ModifyRunnable() {}

		@Override
		public void run() {
			SaverModifyListener.this.getHandler().modify();
		}
	}

	@Override
	public String toString() {
		return String.format("SaverModifyListener [getClass()=%s, toString()=%s]", this.getClass(), super.toString());
	}
}
