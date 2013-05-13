package com.laboki.eclipse.plugin.smartsave;

import com.laboki.eclipse.plugin.smartsave.saver.Factory;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public enum Plugin implements Instance {
	INSTANCE;

	@Override
	public Instance begin() {
		new Task() {

			@Override
			public void asyncExec() {
				Factory.INSTANCE.begin();
			}
		}.begin();
		return this;
	}

	@Override
	public Instance end() {
		new Task() {

			@Override
			public void asyncExec() {
				Factory.INSTANCE.end();
			}
		}.begin();
		return this;
	}
}
