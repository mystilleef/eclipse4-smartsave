package com.laboki.eclipse.plugin.smartsave.saver;

import lombok.Getter;
import lombok.ToString;

import org.eclipse.ui.IEditorPart;

import com.laboki.eclipse.plugin.smartsave.saver.listeners.ISaverFocusListenerHandler;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.ISaverKeyListenerHandler;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.ISaverModifyListenerHandler;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverFocusListener;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverKeyListener;
import com.laboki.eclipse.plugin.smartsave.saver.listeners.SaverModifyListener;

@ToString
final class AutomaticSaver implements Runnable {

	@Getter private final IEditorPart editor = EditorContext.getEditor();
	private final JobScheduler saveScheduler = new JobScheduler("AutoSaveJob");
	private final SaverFocusListener focusListener = new SaverFocusListener(this.new SaverFocusListenerHandler());
	private final SaverModifyListener modifyListener = new SaverModifyListener(this.new SaverModifyListenerHandler());
	private final SaverKeyListener keylisteners = new SaverKeyListener(this.new SaverKeyListenerHandler());

	@Override
	public void run() {
		this.startListeningForPartActivation();
		this.startListeningForBufferModification();
	}

	private void startListeningForPartActivation() {
		this.focusListener.start();
	}

	private void stopListeningForPartActivation() {
		this.focusListener.stop();
	}

	protected void startListeningForBufferModification() {
		this.toggleAutomaticSaving();
		this.modifyListener.start();
	}

	protected void stopListeningForBufferModification() {
		this.stopAutomaticSaving();
		this.modifyListener.stop();
		this.save();
	}

	private void save() {
		this.saveScheduler.save();
	}

	protected void toggleAutomaticSaving() {
		if (this.editor.isDirty()) this.startAutomaticSaving();
		else this.stopAutomaticSaving();
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

	protected void stopAll() {
		this.stopAutomaticSaving();
		this.stopListeningForPartActivation();
		this.stopListeningForBufferModification();
	}

	private final class SaverKeyListenerHandler implements ISaverKeyListenerHandler {

		private final KeyPressRunnable keyPressRunnable = this.new KeyPressRunnable();
		private final KeyReleaseRunnable keyReleaseRunnable = this.new KeyReleaseRunnable();

		public SaverKeyListenerHandler() {}

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

	private final class SaverModifyListenerHandler implements ISaverModifyListenerHandler {

		private final ModifyRunnable modifyRunnable = this.new ModifyRunnable();

		public SaverModifyListenerHandler() {}

		@Override
		public void modify() {
			EditorContext.asyncExec(this.modifyRunnable);
		}

		private final class ModifyRunnable implements Runnable {

			public ModifyRunnable() {}

			@Override
			public void run() {
				AutomaticSaver.this.toggleAutomaticSaving();
			}
		}
	}

	private final class SaverFocusListenerHandler implements ISaverFocusListenerHandler {

		private final Runnable focusGainedRunnable = this.new FocusGainedRunnable();
		private final Runnable focusLostRunnable = this.new FocusLostRunnable();

		public SaverFocusListenerHandler() {}

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
}
