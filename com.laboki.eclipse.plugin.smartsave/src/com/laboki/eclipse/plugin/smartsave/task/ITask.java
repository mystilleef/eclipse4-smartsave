package com.laboki.eclipse.plugin.smartsave.task;

interface ITask {

	void execute();

	void asyncExec();

	void postExecute();
}
