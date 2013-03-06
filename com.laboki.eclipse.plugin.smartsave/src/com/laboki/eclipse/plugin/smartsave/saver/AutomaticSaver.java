package com.laboki.eclipse.plugin.smartsave.saver;

import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.smartsave.saver.listeners.ISaverFocusListenerHandler;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.ISaverKeyListenerHandler;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.ISaverModifyListenerHandler;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverFocusListener;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverKeyListener;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverModifyListener;

final class AutomaticSaver {

	private final IEditorPart editor = EditorContext.getEditor();
	private final SaveJobScheduler saveScheduler = new SaveJobScheduler("AutoSaveJob");
	private final SaverFocusListener focusListener = new SaverFocusListener(this.new AutosaveFocusListenerHandler());
	private final SaverModifyListener modifyListener = new SaverModifyListener(this.new AutosaveModifyListenerHandler());
	private final SaverKeyListener keylisteners = new SaverKeyListener(this.new KeyListenersHandler());

	public void init() {
		this.startListeningForPartActivation();
		this.startListeningForBufferModification();
	}

	private void startListeningForPartActivation() {
		this.focusListener.start();
	}

	@SuppressWarnings("unused")
	private void stopListeningForPartActivation() { // $codepro.audit.disable unusedMethod
		this.focusListener.stop();
	}

	protected void startListeningForBufferModification() {
		this.save();
		this.modifyListener.start();
	}

	protected void stopListeningForBufferModification() {
		this.save();
		this.modifyListener.stop();
	}

	private void save() {
		this.saveScheduler.save();
	}

	protected void startAutomaticSaving() {
		if (!EditorContext.canSaveAutomatically()) return;
		this.startMonitoringEvents();
		this.startSaveSchedule();
	}

	protected void stopAutomaticSaving() {
		this.stopMonitoringEvents();
		this.stopSaveSchedule();
	}

	protected void startSaveSchedule() {
		this.saveScheduler.start();
	}

	protected void stopSaveSchedule() {
		this.saveScheduler.stop();
	}

	private void startMonitoringEvents() {
		this.keylisteners.start();
	}

	private void stopMonitoringEvents() {
		this.keylisteners.stop();
	}

	private final class KeyListenersHandler implements ISaverKeyListenerHandler {

		private final KeyPressRunnable keyPressRunnable = this.new KeyPressRunnable();
		private final KeyReleaseRunnable keyReleaseRunnable = this.new KeyReleaseRunnable();

		public KeyListenersHandler() {}

		@Override
		public void keyPress() {
			EditorContext.asyncExec(this.keyPressRunnable);
		}

		@Override
		public void keyRelease() {
			EditorContext.asyncExec(this.keyReleaseRunnable);
		}

		private final class KeyPressRunnable implements Runnable {

			public KeyPressRunnable() {}

			@Override
			public void run() {
				AutomaticSaver.this.stopSaveSchedule();
			}
		}

		private final class KeyReleaseRunnable implements Runnable {

			public KeyReleaseRunnable() {}

			@Override
			public void run() {
				AutomaticSaver.this.startSaveSchedule();
			}
		}
	}

	private final class AutosaveModifyListenerHandler implements ISaverModifyListenerHandler {

		private final ModifyRunnable modifyRunnable = this.new ModifyRunnable();

		public AutosaveModifyListenerHandler() {}

		@Override
		public void modify() {
			EditorContext.asyncExec(this.modifyRunnable);
		}

		private final class ModifyRunnable implements Runnable {

			public ModifyRunnable() {}

			@Override
			public void run() {
				if (AutomaticSaver.this.getEditor().isDirty()) AutomaticSaver.this.startAutomaticSaving(); // $codepro.audit.disable methodChainLength
				else AutomaticSaver.this.stopAutomaticSaving();
			}
		}
	}

	private final class AutosaveFocusListenerHandler implements ISaverFocusListenerHandler {

		private final Runnable focusGainedRunnable = this.new FocusGainedRunnable();
		private final Runnable focusLostRunnable = this.new FocusLostRunnable();

		public AutosaveFocusListenerHandler() {}

		@Override
		public void focusGained() {
			EditorContext.asyncExec(this.focusGainedRunnable);
		}

		@Override
		public void focusLost() {
			EditorContext.asyncExec(this.focusLostRunnable);
		}

		private final class FocusGainedRunnable implements Runnable {

			public FocusGainedRunnable() {}

			@Override
			public void run() {
				AutomaticSaver.this.startListeningForBufferModification();
			}
		}

		private final class FocusLostRunnable implements Runnable {

			public FocusLostRunnable() {}

			@Override
			public void run() {
				AutomaticSaver.this.stopListeningForBufferModification();
			}
		}
	}

	@Override
	public String toString() {
		return String.format("AutomaticSaver [getClass()=%s]", this.getClass());
	}

	public IEditorPart getEditor() {
		return this.editor;
	}
}
