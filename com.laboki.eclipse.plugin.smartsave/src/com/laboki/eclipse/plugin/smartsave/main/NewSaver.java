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
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.SaveEvent;
import com.laboki.eclipse.plugin.smartsave.instance.Instance;
import com.laboki.eclipse.plugin.smartsave.task.Task;

public final class NewSaver extends WorkspaceJob implements Instance {

	private static final String NAME = "SaverWorkspaceJobName";
	private final Optional<IEditorPart> editor = EditorContext.getEditor();

	public NewSaver() {
		super(NewSaver.NAME);
		this.setProperties();
	}

	private void
	setProperties() {
		this.setPriority(Job.DECORATE);
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
	public Instance
	start() {
		EventBus.register(this);
		return this;
	}

	@Override
	public Instance
	stop() {
		EventBus.unregister(this);
		return this;
	}

	@Override
	public IStatus
	runInWorkspace(final IProgressMonitor monitor) throws CoreException {
		if (monitor.isCanceled()) return Status.CANCEL_STATUS;
		this.save();
		return Status.OK_STATUS;
	}

	protected void
	save() {
		if (!this.editor.isPresent()) return;
		this.editor.get().getSite().getPage().saveEditor(this.editor.get(), false);
	}

	@Override
	public boolean
	belongsTo(final Object family) {
		return family.equals(Scheduler.FAMILY);
	}

	@Override
	public boolean
	shouldSchedule() {
		return NewSaver.currentJobIsBlocking();
	}

	@Override
	public boolean
	shouldRun() {
		return NewSaver.currentJobIsBlocking();
	}

	private static boolean
	currentJobIsBlocking() {
		final Job job = EditorContext.JOB_MANAGER.currentJob();
		if (job == null) return true;
		return !job.isBlocking();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void
	eventHandler(final SaveEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				NewSaver.this.schedule(Scheduler.DELAY);
			}
		}.setFamily(Scheduler.FAMILY)
			.setRule(Scheduler.RULE)
			.setDelay(Scheduler.DELAY)
			.start();
	}
}
