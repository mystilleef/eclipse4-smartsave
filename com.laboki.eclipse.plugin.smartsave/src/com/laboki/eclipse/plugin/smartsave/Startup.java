package com.laboki.eclipse.plugin.smartsave;

import org.eclipse.ui.IPartService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.Factory;

public final class Startup implements IStartup, Runnable {

	public Startup() {}

	@Override
	public void earlyStartup() {
		EditorContext.asyncExec(this);
	}

	@Override
	public void run() {
		Startup.start();
	}

	@SuppressWarnings("unused")
	private static void start() {
		new Factory((IPartService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IPartService.class));
	}
}
