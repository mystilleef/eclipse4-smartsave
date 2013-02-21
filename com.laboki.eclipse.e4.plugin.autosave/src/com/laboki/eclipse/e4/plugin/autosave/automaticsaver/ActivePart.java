package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import com.laboki.eclipse.e4.plugin.autosave.AddonMetadata;

public final class ActivePart {

	private ActivePart() {}

	public static StyledText getBuffer() {
		return (StyledText) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getAdapter(Control.class);
	}

	public static boolean isInvalid(final MPart activePart) {
		if (ActivePart.isNotAnEditor(activePart)) return true;
		if (ActivePart.isTagged(activePart)) return true;
		return false;
	}

	public static boolean isNotAnEditor(final MPart activePart) {
		if (activePart == null) return true;
		if (activePart.getTags().contains("Editor")) return false;
		return true;
	}

	public static boolean isTagged(final MPart activePart) {
		if (activePart == null) return true;
		if (activePart.getContext().containsKey(AddonMetadata.PLUGIN_NAME)) return true;
		return false;
	}

	public static boolean isNotTagged(final MPart activePart) {
		return !ActivePart.isTagged(activePart);
	}
}
