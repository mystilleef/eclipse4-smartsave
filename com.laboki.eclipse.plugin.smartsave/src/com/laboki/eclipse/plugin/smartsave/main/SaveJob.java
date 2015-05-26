package com.laboki.eclipse.plugin.smartsave.main;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;

public class SaveJob extends WorkspaceJob implements Runnable {

	private static final String NAME = "SmartSaveSaveJobName";
	private Optional<IEditorPart> editor;

	public SaveJob() {
		super(SaveJob.NAME);
		this.setProperties();
	}

	private void
	setProperties() {
		this.setPriority(Job.DECORATE);
		this.setUser(false);
		this.setSystem(true);
		this.setRule(ResourcesPlugin.getWorkspace().getRoot());
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
	shouldSchedule() {
		return SaveJob.currentJobIsBlocking();
	}

	@Override
	public boolean
	shouldRun() {
		return SaveJob.currentJobIsBlocking();
	}

	private static boolean
	currentJobIsBlocking() {
		final Job job = EditorContext.JOB_MANAGER.currentJob();
		if (job == null) return true;
		return !job.isBlocking();
	}

	@Override
	public boolean
	belongsTo(final Object family) {
		return family.equals(Scheduler.FAMILY);
	}

	public void
	execute(final Optional<IEditorPart> editorPart) {
		// this.setNewRule(editorPart);
		this.editor = editorPart;
		this.schedule(Scheduler.DELAY);
	}

	protected void
	setNewRule(final Optional<IEditorPart> editorPart) {
		if (this.editor == editorPart) return;
		if (!editorPart.isPresent()) return;
		final Optional<IFile> file = EditorContext.getFile(editorPart);
		if (file.isPresent()) this.setRule(file.get());
	}
}
