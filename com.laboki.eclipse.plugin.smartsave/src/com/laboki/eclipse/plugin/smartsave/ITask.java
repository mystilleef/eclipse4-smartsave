package com.laboki.eclipse.plugin.smartsave;

interface ITask {

	void execute();

	void asyncExec();

	void postExecute();
}
