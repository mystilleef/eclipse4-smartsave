package com.laboki.eclipse.plugin.smartsave;

import java.text.MessageFormat;

public final class Metadata {

	public static final String PLUGIN_NAME = "com.laboki.eclipse.plugin.smartsave";
	public static final String CONTRIBUTION_URI = "bundleclass://{0}/{1}";
	public static final String CONTRIBUTOR_URI = MessageFormat.format("plugin://{0}", Metadata.PLUGIN_NAME);

	private Metadata() {}
}
