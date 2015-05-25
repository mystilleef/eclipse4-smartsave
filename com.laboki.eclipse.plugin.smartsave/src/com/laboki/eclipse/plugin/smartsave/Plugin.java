package com.laboki.eclipse.plugin.smartsave;

import org.eclipse.core.runtime.jobs.Job;

import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.main.Services;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public enum Plugin implements Instance {
	INSTANCE;

	protected static final Services SERVICES = new Services();

	@Override
	public Instance
	start() {
		new AsyncTask() {

			@Override
			public void
			execute() {
				Plugin.SERVICES.start();
			}
		}.setPriority(Job.INTERACTIVE).start();
		return this;
	}

	@Override
	public Instance
	stop() {
		Plugin.SERVICES.stop();
		return this;
	}
}
