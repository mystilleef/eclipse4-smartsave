package com.laboki.eclipse.plugin.smartsave.main;

import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;

public enum Factory implements Instance {
	INSTANCE;

	private static final Map<IEditorPart, Instance> SERVICES_MAP =
		Maps.newHashMap();
	private static final Optional<IPartService> PART_SERVICE =
		EditorContext.getPartService();
	private static final PartListener PART_LISTENER = new PartListener();

	private static final class PartListener implements IPartListener {

		public PartListener() {}

		@Override
		public void
		partActivated(final IWorkbenchPart part) {
			Factory.enableAutomaticSaverFor(part);
		}

		@Override
		public void
		partClosed(final IWorkbenchPart part) {}

		@Override
		public void
		partBroughtToTop(final IWorkbenchPart part) {}

		@Override
		public void
		partDeactivated(final IWorkbenchPart part) {
			if (part instanceof IEditorPart) PartListener.forceSave(part);
			Factory.stopAllSaverServices();
		}

		private static void
		forceSave(final IWorkbenchPart part) {
			EditorContext.forceSave(Optional.fromNullable((IEditorPart) part));
		}

		@Override
		public void
		partOpened(final IWorkbenchPart part) {}
	}

	@Override
	public Instance
	start() {
		if (!Factory.PART_SERVICE.isPresent()) return this;
		Factory.enableAutomaticSaverFor(Factory.PART_SERVICE.get().getActivePart());
		Factory.PART_SERVICE.get().addPartListener(Factory.PART_LISTENER);
		return this;
	}

	@Override
	public Instance
	stop() {
		if (!Factory.PART_SERVICE.isPresent()) return this;
		Factory.PART_SERVICE.get().removePartListener(Factory.PART_LISTENER);
		Factory.stopAllSaverServices();
		return this;
	}

	static void
	enableAutomaticSaverFor(final IWorkbenchPart part) {
		if (Factory.isInvalidPart(part)) return;
		Factory.startSaverServiceFor(part);
	}

	private static boolean
	isInvalidPart(final IWorkbenchPart part) {
		return !Factory.isValidPart(part);
	}

	private static boolean
	isValidPart(final IWorkbenchPart part) {
		if (Factory.isNotEditorPart(part)) return false;
		return true;
	}

	private static boolean
	isNotEditorPart(final IWorkbenchPart part) {
		return !Factory.isEditorPart(part);
	}

	private static boolean
	isEditorPart(final IWorkbenchPart part) {
		return part instanceof IEditorPart;
	}

	private static void
	startSaverServiceFor(final IWorkbenchPart part) {
		Factory.stopAllSaverServices();
		Factory.SERVICES_MAP.put((IEditorPart) part, new Services().start());
	}

	static void
	stopAllSaverServices() {
		for (final IEditorPart part : Factory.SERVICES_MAP.keySet())
			Factory.stopSaverServiceFor(part);
	}

	private static void
	stopSaverServiceFor(final IWorkbenchPart part) {
		if (Factory.servicesMapDoesNotContain(part)) return;
		Factory.SERVICES_MAP.remove(part).stop();
	}

	private static boolean
	servicesMapDoesNotContain(final IWorkbenchPart part) {
		return !Factory.SERVICES_MAP.containsKey(part);
	}
}
