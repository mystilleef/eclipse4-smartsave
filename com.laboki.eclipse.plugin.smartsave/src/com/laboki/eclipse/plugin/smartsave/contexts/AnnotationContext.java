package com.laboki.eclipse.plugin.smartsave.contexts;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public enum AnnotationContext {
	INSTANCE;

	private static final String WARNING =
		"org.eclipse.ui.workbench.texteditor.warning";
	private static final String ERROR =
		"org.eclipse.ui.workbench.texteditor.error";
	private static final List<String> LINKS =
		Lists.newArrayList("org.eclipse.ui.internal.workbench.texteditor.link.slave",
			"org.eclipse.ui.internal.workbench.texteditor.link.master",
			"org.eclipse.ui.internal.workbench.texteditor.link.target",
			"org.eclipse.ui.internal.workbench.texteditor.link.exit");
	private static final DefaultMarkerAnnotationAccess ACCESS =
		new DefaultMarkerAnnotationAccess();

	public static boolean
	isInLinkMode(final Optional<IEditorPart> editor) {
		return AnnotationContext.hasLinkAnnotations(editor);
	}

	private static boolean
	hasLinkAnnotations(final Optional<IEditorPart> editor) {
		final Optional<SourceViewer> view = EditorContext.getView(editor);
		if (!view.isPresent()) return false;
		final Iterator<Annotation> iterator = AnnotationContext.getIterator(view);
		while (iterator.hasNext())
			if (AnnotationContext.isLinkModeAnnotation(iterator)) return true;
		return false;
	}

	private static boolean
	isLinkModeAnnotation(final Iterator<Annotation> iterator) {
		if (AnnotationContext.LINKS.contains(iterator.next().getType())) return true;
		return false;
	}

	public static boolean
	hasErrors(final Optional<IEditorPart> editor) {
		return AnnotationContext.getAnnotationSeverity(AnnotationContext.ERROR,
			editor);
	}

	public static boolean
	hasWarnings(final Optional<IEditorPart> editor) {
		return AnnotationContext.getAnnotationSeverity(AnnotationContext.WARNING,
			editor);
	}

	private static boolean
	getAnnotationSeverity(final String problemSeverity,
												final Optional<IEditorPart> editor) {
		final Optional<SourceViewer> view = EditorContext.getView(editor);
		if (!view.isPresent()) return false;
		final Iterator<Annotation> iterator = AnnotationContext.getIterator(view);
		while (iterator.hasNext())
			if (AnnotationContext.hasProblems(problemSeverity, iterator)) return true;
		return false;
	}

	private static Iterator<Annotation>
	getIterator(final Optional<SourceViewer> view) {
		return view.get().getAnnotationModel().getAnnotationIterator();
	}

	private static boolean
	hasProblems(final String problemSeverity, final Iterator<Annotation> iterator) {
		final Annotation annotation = iterator.next();
		if (annotation.isMarkedDeleted()) return false;
		return AnnotationContext.ACCESS.isSubtype(annotation.getType(),
			problemSeverity);
	}
}
