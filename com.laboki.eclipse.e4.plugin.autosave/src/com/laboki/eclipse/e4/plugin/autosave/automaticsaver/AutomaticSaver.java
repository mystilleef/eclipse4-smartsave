package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.widgets.Display;

class AutomaticSaver {

	private final IEventBroker eventBroker;
	private final Listener bufferModificationListener;
	private final SaveJobScheduler saveScheduler = new SaveJobScheduler("AutoSaveJob");
	private final KeyListeners keylisteners = new KeyListeners(this.new MyKeyListenersHandler());
	private final AutosaveFocusListener focusListener = new AutosaveFocusListener(this.new AutosaveFocusListenerHandler());
	private static final String EDITOR_IS_MODIFIED = UIEvents.Dirtyable.TOPIC_DIRTY;

	public AutomaticSaver(final MPart editorPart) {
		this.eventBroker = editorPart.getContext().get(IEventBroker.class);
		this.bufferModificationListener = new Listener(AutomaticSaver.EDITOR_IS_MODIFIED, new BufferModificationHandler(editorPart), this.eventBroker);
	}

	public void init() {
		this.startListeningForPartActivation();
		this.startListeningForBufferModification();
	}

	private void startListeningForPartActivation() {
		this.focusListener.start();
	}

	@SuppressWarnings("unused")
	private void stopListeningForPartActivation() {
		this.focusListener.stop();
	}

	protected void startListeningForBufferModification() {
		this.save();
		this.bufferModificationListener.start();
	}

	protected void stopListeningForBufferModification() {
		this.save();
		this.bufferModificationListener.stop();
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
		ActivePart.flushEvents();
		this.saveScheduler.start();
	}

	protected void stopSaveSchedule() {
		this.saveScheduler.stop();
		ActivePart.flushEvents();
	}

	private void startMonitoringEvents() {
		this.keylisteners.start();
	}

	private void stopMonitoringEvents() {
		this.keylisteners.stop();
	}

	private final class MyKeyListenersHandler implements KeyListenersHandler {

		public MyKeyListenersHandler() {}

		@Override
		public void keyPress() {
			Display.getDefault().asyncExec(this.new KeyPressRunnable());
		}

		@Override
		public void keyRelease() {
			Display.getDefault().asyncExec(this.new KeyReleaseRunnable());
		}

		private final class KeyPressRunnable implements Runnable {

			public KeyPressRunnable() {}

			@Override
			public void run() {
				AutomaticSaver.this.stopSaveSchedule();
			}
		}

		final class KeyReleaseRunnable implements Runnable {

			public KeyReleaseRunnable() {}

			@Override
			public void run() {
				AutomaticSaver.this.startSaveSchedule();
			}
		}
	}

	private final class BufferModificationHandler implements ListenerHandler {

		private final MPart editor;

		public BufferModificationHandler(final MPart editorPart) {
			this.editor = editorPart;
		}

		@Override
		public void handle(final Object event) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (BufferModificationHandler.this.getEditor().isDirty()) AutomaticSaver.this.startAutomaticSaving();
					else AutomaticSaver.this.stopAutomaticSaving();
				}
			});
		}

		public MPart getEditor() {
			return this.editor;
		}
	}

	private final class AutosaveFocusListenerHandler implements IAutosaveFocusListenerHandler {

		public AutosaveFocusListenerHandler() {}

		@Override
		public void focusGained() {
			AutomaticSaver.this.startListeningForBufferModification();
			System.out.print("focus gained: ");
		}

		@Override
		public void focusLost() {
			AutomaticSaver.this.stopListeningForBufferModification();
			System.out.print("focus lost: ");
		}
	}

	@Override
	public String toString() {
		return String.format("AutomaticSaver [getClass()=%s]", this.getClass());
	}
}
