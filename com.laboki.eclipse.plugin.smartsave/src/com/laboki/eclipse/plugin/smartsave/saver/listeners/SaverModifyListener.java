package com.laboki.eclipse.plugin.smartsave.saver.listeners;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

@ToString
public final class SaverModifyListener extends AbstractSaverListener implements IPropertyListener {

	@Getter private final ISaverModifyListenerHandler handler;
	private final ModifyRunnable modifyRunnable = new ModifyRunnable();
	private final IEditorPart editorBuffer = EditorContext.getEditor();

	public SaverModifyListener(final ISaverModifyListenerHandler handler) {
		this.handler = handler;
	}

	@Override
	public void add() {
		this.editorBuffer.addPropertyListener(this);
	}

	@Override
	public void remove() {
		this.editorBuffer.removePropertyListener(this);
	}

	@Override
	public void propertyChanged(final Object source, final int propID) {
		if (propID == IEditorPart.PROP_DIRTY) EditorContext.asyncExec(this.modifyRunnable);
	}

	private final class ModifyRunnable implements Runnable {

		public ModifyRunnable() {}

		@Override
		public void run() {
			SaverModifyListener.this.getHandler().modify();
		}
	}
}
