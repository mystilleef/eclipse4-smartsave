package com.laboki.eclipse.plugin.smartsave;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public final class Activator extends AbstractUIPlugin {

	private static Activator instance;

	public Activator() {
		Activator.instance = this;
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		Plugin.INSTANCE.begin();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		Activator.instance = null;
		Plugin.INSTANCE.end();
		super.stop(context);
	}

	public static Activator getInstance() {
		return Activator.instance;
	}
}
