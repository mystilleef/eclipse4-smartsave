// $codepro.audit.disable methodChainLength
package com.laboki.eclipse.plugin.smartsave.saver;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import com.laboki.eclipse.plugin.smartsave.Metadata;

public final class SaverFactory {

	public SaverFactory() {
		EditorContext.instance();
	}

	@Inject
	@Optional
	public static void activateHandler(@UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) final Event event) {
		final MPart activePart = (MPart) event.getProperty(UIEvents.EventTags.ELEMENT);
		if (SaverFactory.isInvalid(activePart)) return;
		SaverFactory.enableAutomaticSaverFor(activePart);
	}

	private static boolean isInvalid(final MPart activePart) {
		if (SaverFactory.isNotAnEditor(activePart)) return true;
		if (SaverFactory.isTagged(activePart)) return true;
		return false;
	}

	private static boolean isNotAnEditor(final MPart activePart) {
		if (activePart == null) return true;
		if (activePart.getTags().contains("Editor")) return false;
		return true;
	}

	private static boolean isTagged(final MPart activePart) {
		if (activePart == null) return true;
		if (activePart.getContext().containsKey(Metadata.PLUGIN_NAME)) return true;
		return false;
	}

	private static void enableAutomaticSaverFor(final MPart activePart) {
		final AutomaticSaver automaticSaver = new AutomaticSaver();
		activePart.getContext().set(Metadata.PLUGIN_NAME, automaticSaver);
		automaticSaver.init();
	}

	@Override
	public String toString() {
		return String.format("Addon [getClass()=%s, hashCode()=%s, toString()=%s]", this.getClass(), Integer.valueOf(this.hashCode()), super.toString());
	}
}
