package com.laboki.eclipse.plugin.smartsave;

import com.laboki.eclipse.plugin.smartsave.saver.Factory;

public enum Saver implements Runnable {
	INSTANCE;

	@Override
	public void run() {
		Factory.INSTANCE.begin();
	}
}
