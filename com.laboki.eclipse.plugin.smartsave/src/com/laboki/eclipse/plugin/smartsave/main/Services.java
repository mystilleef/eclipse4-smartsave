package com.laboki.eclipse.plugin.smartsave.main;

import com.laboki.eclipse.plugin.smartsave.main.services.BaseServices;

public final class Services extends BaseServices {

	@Override
	protected void
	startServices() {
		this.startService(new PartMonitor());
	}
}
