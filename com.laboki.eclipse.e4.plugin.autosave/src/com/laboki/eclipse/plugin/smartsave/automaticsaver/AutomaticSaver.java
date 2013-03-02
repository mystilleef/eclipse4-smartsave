package com.laboki.eclipse.plugin.smartsave.automaticsaver;

import org.eclipse.ui.IEditorPart;

final class AutomaticSaver {

	private final IEditorPart editor = ActivePart.getEditor();
	private final SaveJobScheduler saveScheduler = new SaveJobScheduler("AutoSaveJob");
	private final AutosaveFocusListener focusListener = new AutosaveFocusListener(this.new AutosaveFocusListenerHandler());
	private final AutosaveModifyListener modifyListener = new AutosaveModifyListener(this.new AutosaveModifyListenerHandler());
	private final AutosaveKeyListener keylisteners = new AutosaveKeyListener(this.new KeyListenersHandler());

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
		if (!ActivePart.canSaveAutomatically()) return;
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

	private final class KeyListenersHandler implements IAutosaveKeyListenersHandler {

		private final KeyPressRunnable keyPressRunnable = this.new KeyPressRunnable();
		private final KeyReleaseRunnable keyReleaseRunnable = this.new KeyReleaseRunnable();

		public KeyListenersHandler() {}

		@Override
		public void keyPress() {
			ActivePart.asyncExec(this.keyPressRunnable);
		}

		@Override
		public void keyRelease() {
			ActivePart.asyncExec(this.keyReleaseRunnable);
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

	private final class AutosaveModifyListenerHandler implements IAutosaveModifyListenerHandler {

		private final ModifyRunnable modifyRunnable = this.new ModifyRunnable();

		public AutosaveModifyListenerHandler() {}

		@Override
		public void modify() {
			ActivePart.asyncExec(this.modifyRunnable);
		}

		private final class ModifyRunnable implements Runnable {

			public ModifyRunnable() {}

			@Override
			public void run() {
				if (AutomaticSaver.this.getEditor().isDirty()) AutomaticSaver.this.startAutomaticSaving();
				else AutomaticSaver.this.stopAutomaticSaving();
			}
		}
	}

	private final class AutosaveFocusListenerHandler implements IAutosaveFocusListenerHandler {

		private final Runnable focusGainedRunnable = this.new FocusGainedRunnable();
		private final Runnable focusLostRunnable = this.new FocusLostRunnable();

		public AutosaveFocusListenerHandler() {}

		@Override
		public void focusGained() {
			ActivePart.asyncExec(this.focusGainedRunnable);
		}

		@Override
		public void focusLost() {
			ActivePart.asyncExec(this.focusLostRunnable);
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
