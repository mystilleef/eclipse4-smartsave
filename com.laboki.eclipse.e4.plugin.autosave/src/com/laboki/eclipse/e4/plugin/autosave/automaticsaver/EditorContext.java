package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

final class EditorContext {

	private final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	private final SourceViewer textView = (SourceViewer) this.editor.getAdapter(ITextOperationTarget.class);
	private final IFile file = ((IFileEditorInput) this.editor.getEditorInput()).getFile();

	void save() {
		this.editor.doSave(null);
	}

	void printAnnotationTypes() {
		final Iterator<Annotation> iterator = this.textView.getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			System.out.println(iterator.next().getType());
	}

	boolean isModified() {
		return this.editor.isDirty();
	}

	private boolean getAnnotationSeverity(final String problemSeverity) {
		final Iterator<Annotation> iterator = this.textView.getAnnotationModel().getAnnotationIterator();
		while (iterator.hasNext())
			if (iterator.next().getType().endsWith(problemSeverity)) return true;
		return false;
	}

	boolean hasWarnings() {
		return this.getAnnotationSeverity("warning");
	}

	boolean hasErrors() {
		return this.getAnnotationSeverity("error");
	}

	static boolean canCheckWarnings() {
		return false;
	}

	static boolean canCheckErrors() {
		return true;
	}

	IFile getFile() {
		return this.file;
	}

	StyledText getTextBuffer() {
		return this.textView.getTextWidget();
	}
}
