package com.laboki.eclipse.e4.plugin.autosave;

import java.text.MessageFormat;

public final class AddonMetadata {

	public static final String PLUGIN_NAME = "com.laboki.eclipse.e4.plugin.autosave";
	public static final String CONTRIBUTION_URI = "bundleclass://{0}/{1}";
	public static final String CONTRIBUTOR_URI = MessageFormat.format("plugin://{0}", AddonMetadata.PLUGIN_NAME);

	private AddonMetadata() {}
}
