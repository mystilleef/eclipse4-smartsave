package com.laboki.eclipse.plugin.smartsave.saver;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import com.laboki.eclipse.plugin.smartsave.AddonMetadata;

public final class AutomaticSaverInitializer {

	public AutomaticSaverInitializer() {
		ActivePart.initialize();
	}

	@Inject
	@Optional
	public static void activateHandler(@UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) final Event event) {
		final MPart activePart = (MPart) event.getProperty(UIEvents.EventTags.ELEMENT);
		if (ActivePart.isInvalid(activePart)) return;
		AutomaticSaverInitializer.enableAutomaticSaverFor(activePart);
	}

	private static void enableAutomaticSaverFor(final MPart activePart) {
		final AutomaticSaver automaticSaver = new AutomaticSaver();
		activePart.getContext().set(AddonMetadata.PLUGIN_NAME, automaticSaver);
		automaticSaver.init();
	}

	@Override
	public String toString() {
		return String.format("Addon [getClass()=%s, hashCode()=%s, toString()=%s]", this.getClass(), Integer.valueOf(this.hashCode()), super.toString());
	}
}
