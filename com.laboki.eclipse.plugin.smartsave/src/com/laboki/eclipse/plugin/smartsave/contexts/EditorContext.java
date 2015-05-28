package com.laboki.eclipse.plugin.smartsave.contexts;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Optional;
import com.laboki.eclipse.plugin.smartsave.Activator;
import com.laboki.eclipse.plugin.smartsave.listeners.BaseListener;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.main.Scheduler;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.BaseTask;

public enum EditorContext {
	INSTANCE;

	public static final String PLUGIN_ID = Activator.PLUGIN_ID;
	public static final IJobManager JOB_MANAGER = Job.getJobManager();
	public static final IWorkbench WORKBENCH = PlatformUI.getWorkbench();
	public static final Display DISPLAY = EditorContext.WORKBENCH.getDisplay();
	private static final int MILLI_SECONDS_UNIT = 1000;

	public static Optional<Shell>
	getShell() {
		return Optional.fromNullable(EditorContext.WORKBENCH.getModalDialogShellProvider()
			.getShell());
	}

	public static Optional<IPartService>
	getPartService() {
		final Optional<IWorkbenchWindow> window =
			EditorContext.getActiveWorkbenchWindow();
		if (!window.isPresent()) return Optional.absent();
		return Optional.fromNullable((IPartService) window.get()
			.getService(IPartService.class));
	}

	public static Optional<IEditorPart>
	getEditor() {
		final Optional<IWorkbenchWindow> window =
			EditorContext.getActiveWorkbenchWindow();
		if (!window.isPresent()) return Optional.absent();
		final Optional<IWorkbenchPage> page = EditorContext.getActivePage(window);
		if (!page.isPresent()) return Optional.absent();
		return Optional.fromNullable(page.get().getActiveEditor());
	}

	private static Optional<IWorkbenchWindow>
	getActiveWorkbenchWindow() {
		return Optional.fromNullable(EditorContext.WORKBENCH.getActiveWorkbenchWindow());
	}

	private static Optional<IWorkbenchPage>
	getActivePage(final Optional<IWorkbenchWindow> activeWorkbenchWindow) {
		return Optional.fromNullable(activeWorkbenchWindow.get().getActivePage());
	}

	public static Optional<SourceViewer>
	getView(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		return Optional.fromNullable((SourceViewer) editor.get()
			.getAdapter(ITextOperationTarget.class));
	}

	public static Optional<StyledText>
	getBuffer(final Optional<IEditorPart> editor) {
		final Optional<Control> control = EditorContext.getControl(editor);
		if (!control.isPresent()) return Optional.absent();
		return Optional.fromNullable((StyledText) EditorContext.getControl(editor)
			.get());
	}

	public static Optional<Control>
	getControl(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return Optional.absent();
		return Optional.fromNullable((Control) editor.get()
			.getAdapter(Control.class));
	}

	public static Optional<IFile>
	getFile(final Optional<IEditorPart> editor) {
		return FileContext.getFile(editor);
	}

	public static void
	savePart(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return;
		editor.get().getSite().getPage().saveEditor(editor.get(), false);
	}

	public static boolean
	isEditorPart(final IWorkbenchPart part) {
		return part instanceof IEditorPart;
	}

	public static boolean
	canSaveAutomatically(final Optional<IEditorPart> editor) {
		if (!editor.isPresent()) return false;
		if (!Store.getCanSaveAutomatically()) return false;
		if (EditorContext.isBlacklisted(editor)) return false;
		return true;
	}

	public static void
	toggleCanSaveAutomatically() {
		Store.toggleCanSaveAutomatically();
	}

	public static void
	setCanSaveAutomatically(final boolean canSave) {
		Store.setCanSaveAutomatically(canSave);
	}

	public static int
	getSaveIntervalInMilliSeconds() {
		return (Store.getSaveIntervalInSeconds() * EditorContext.MILLI_SECONDS_UNIT);
	}

	public static ArrayList<String>
	getBlacklist() {
		final String blacklist = Store.getContentTypeBlacklist();
		if (blacklist.isEmpty()) return new ArrayList<>();
		return new ArrayList<>(Arrays.asList(blacklist.split(";")));
	}

	public static void
	setBlacklist(final String contentTypes) {
		Store.setContentTypeBlacklist(contentTypes);
	}

	public static boolean
	isBlacklisted(final Optional<IEditorPart> editor) {
		final ArrayList<String> blacklist = EditorContext.getBlacklist();
		if (blacklist.isEmpty()) return false;
		if (blacklist.contains(ContentTypeContext.getContentTypeId(editor))) return true;
		return false;
	}

	public static IContentType[]
	getContentTypes() {
		return ContentTypeContext.getContentTypes();
	}

	public static void
	asyncExec(final Runnable runnable) {
		if (EditorContext.displayDoesNotExist()) return;
		EditorContext.DISPLAY.asyncExec(runnable);
	}

	public static void
	syncExec(final Runnable runnable) {
		if (EditorContext.displayDoesNotExist()) return;
		EditorContext.DISPLAY.syncExec(runnable);
	}

	private static boolean
	displayDoesNotExist() {
		return !EditorContext.displayExists();
	}

	private static boolean
	displayExists() {
		return !EditorContext.displayIsDisposed();
	}

	private static boolean
	displayIsDisposed() {
		if (EditorContext.DISPLAY == null) return true;
		return EditorContext.DISPLAY.isDisposed();
	}

	public static void
	cancelSaverTasks() {
		EditorContext.JOB_MANAGER.cancel(Scheduler.FAMILY);
		EditorContext.JOB_MANAGER.cancel(BaseListener.FAMILY);
	}

	public static void
	cancelEventTasks() {
		EditorContext.JOB_MANAGER.cancel(EventBus.FAMILY);
	}

	public static void
	cancelPluginTasks() {
		EditorContext.JOB_MANAGER.cancel(BaseTask.FAMILY);
	}
}
