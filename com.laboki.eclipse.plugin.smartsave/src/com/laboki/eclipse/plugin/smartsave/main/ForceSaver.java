package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class ForceSaver extends WorkspaceJob {

	private final Optional<IEditorPart> editor;

	public ForceSaver(final IEditorPart editor) {
		super("");
		this.editor = Optional.fromNullable(editor);
		this.setProperties();
	}

	private void
	setProperties() {
		this.setPriority(Job.INTERACTIVE);
		this.setUser(false);
		this.setSystem(true);
		this.updateRule();
	}

	private void
	updateRule() {
		final Optional<IFile> file = EditorContext.getFile(this.editor);
		if (file.isPresent()) this.setRule(file.get());
	}

	@Override
	public IStatus
	runInWorkspace(final IProgressMonitor monitor) throws CoreException {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		this.startSaveTask();
		return Status.OK_STATUS;
	}

	private void
	startSaveTask() {
		new AsyncTask() {

			@Override
			public void
			execute() {
				EditorContext.savePart(ForceSaver.this.editor);
			}
		}.setFamily("").setPriority(Job.INTERACTIVE).start();
	}

	public void
	save() {
		this.schedule();
	}
}
