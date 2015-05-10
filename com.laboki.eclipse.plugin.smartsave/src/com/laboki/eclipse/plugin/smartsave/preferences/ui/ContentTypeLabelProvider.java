package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public final class ContentTypeLabelProvider implements ILabelProvider {

	@Override
	public void
	addListener(final ILabelProviderListener listener) {}

	@Override
	public void
	dispose() {}

	@Override
	public boolean
	isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void
	removeListener(final ILabelProviderListener listener) {}

	@Override
	public Image
	getImage(final Object element) {
		return null;
	}

	@Override
	public String
	getText(final Object element) {
		return ((IContentType) element).getName();
	}
}
