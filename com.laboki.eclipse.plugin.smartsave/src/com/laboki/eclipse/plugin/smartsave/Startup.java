package com.laboki.eclipse.plugin.smartsave;

import lombok.ToString;

import org.eclipse.ui.IStartup;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;

@ToString
public final class Startup implements IStartup, Runnable {

	public Startup() {}

	@Override
	public void earlyStartup() {
		EditorContext.asyncExec(this);
	}

	@Override
	public void run() {
		EditorContext.asyncExec(Saver.INSTANCE);
	}
}
