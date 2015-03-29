
package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

abstract class ResponseComboViewer extends ComboViewer {

  private final LabelProvider labelProvider = new ResponseLabelProvider();
  private final ISelectionChangedListener listener =
      new ResponseSelectionListener();
  private final Response[] responses = new Response[] {
    new Response(Response.YES), new Response(Response.NO),
  };

  protected ResponseComboViewer(final Composite parent) {
    super(parent, SWT.READ_ONLY);
    this.updateProperties();
  }

  private void updateProperties() {
    this.setContentProvider(ArrayContentProvider.getInstance());
    this.setLabelProvider(this.labelProvider);
    this.setInput(this.responses);
  }

  protected void startListening() {
    this.addSelectionChangedListener(this.listener);
  }

  protected void stopListening() {
    this.removeSelectionChangedListener(this.listener);
  }

  protected void handleResponseSelection(
    @SuppressWarnings("unused") final SelectionChangedEvent event) {}

  protected Response[] getResponses() {
    return this.responses;
  }

  private final static class ResponseLabelProvider extends LabelProvider {

    public ResponseLabelProvider() {}

    @Override
    public String getText(final Object element) {
      return ((Response) element).string();
    }
  }

  private final class ResponseSelectionListener implements
  ISelectionChangedListener {

    public ResponseSelectionListener() {}

    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
      ResponseComboViewer.this.handleResponseSelection(event);
    }
  }

  protected final static class Response {

    private boolean value;
    public static final String YES = "yes";
    public static final String NO = "no";

    public Response(final String response) {
      if (response.equals(Response.YES)) this.value = true;
    }

    public boolean value() {
      return this.value;
    }

    public String string() {
      if (this.value) return "Yes";
      return "No";
    }
  }
}
