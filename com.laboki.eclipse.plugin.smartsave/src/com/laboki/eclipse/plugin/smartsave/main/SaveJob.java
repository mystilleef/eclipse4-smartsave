package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;

public class SaveJob extends WorkspaceJob implements Runnable {

	private static final String TASK_NAME = "SMART_SAVE_WORKSPACE_SAVE_JOB";
	public static final String JOB_FAMILY = "++SAVE_WORKSPACE_JOB_FAMILY++";
	private Optional<IEditorPart> editor;

	public SaveJob() {
		super(SaveJob.TASK_NAME);
		this.setProperties();
	}

	private void
	setProperties() {
		this.setPriority(Job.DECORATE);
		this.setUser(false);
		this.setSystem(true);
	}

	@Override
	public void
	run() {
		this.save();
	}

	protected void
	save() {
		if (!this.editor.isPresent()) return;
		this.editor.get().getSite().getPage().saveEditor(this.editor.get(), false);
	}

	@Override
	public IStatus
	runInWorkspace(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		EditorContext.asyncExec(this);
		return Status.OK_STATUS;
	}

	@Override
	public boolean
	belongsTo(final Object family) {
		return family.equals(SaveJob.JOB_FAMILY);
	}

	@Override
	public boolean
	shouldSchedule() {
		if (EditorContext.currentJobIsBlocking()) return false;
		return EditorContext.canScheduleSave();
	}

	public static boolean
	doesNotExists() {
		return Job.getJobManager().find(SaveJob.JOB_FAMILY).length == 0;
	}

	public void
	execute(final Optional<IEditorPart> editorPart) {
		this.setNewRule(editorPart);
		this.editor = editorPart;
		this.schedule(EditorContext.SHORT_DELAY);
	}

	private void
	setNewRule(final Optional<IEditorPart> editorPart) {
		if (this.editor == editorPart) return;
		if (!editorPart.isPresent()) return;
		final Optional<IFile> file =
			EditorContext.getFile(Optional.fromNullable(editorPart.get()));
		if (!file.isPresent()) return;
		this.setRule(file.get());
	}
}
