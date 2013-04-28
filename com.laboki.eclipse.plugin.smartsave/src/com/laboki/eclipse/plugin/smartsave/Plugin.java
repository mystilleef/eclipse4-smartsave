package com.laboki.eclipse.plugin.smartsave;

import com.laboki.eclipse.plugin.smartsave.saver.EditorContext;
import com.laboki.eclipse.plugin.smartsave.saver.Factory;

public enum Plugin implements Instance {
	INSTANCE;

	@Override
	public Instance begin() {
		EditorContext.asyncExec(new Task() {

			@Override
			public void asyncExec() {
				Factory.INSTANCE.begin();
			}
		});
		return this;
	}

	@Override
	public Instance end() {
		EditorContext.asyncExec(new Task() {

			@Override
			public void asyncExec() {
				Factory.INSTANCE.end();
			}
		});
		return this;
	}
}
