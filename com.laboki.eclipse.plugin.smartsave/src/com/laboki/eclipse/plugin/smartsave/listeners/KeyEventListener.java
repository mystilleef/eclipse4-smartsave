package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

import com.laboki.eclipse.plugin.smartsave.listeners.abstraction.AbstractListener;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;

public final class KeyEventListener extends AbstractListener implements
    KeyListener {

  private final Control control = EditorContext.getControl(EditorContext
      .getEditor());

  public KeyEventListener() {
    super();
  }

  @Override
  public void add() {
    if (this.control == null) return;
    this.control.addKeyListener(this);
  }

  @Override
  public void remove() {
    if (this.control == null) return;
    this.control.removeKeyListener(this);
  }

  @Override
  public void keyPressed(final KeyEvent event) {
    EditorContext.cancelAllSaverTasks();
  }

  @Override
  public void keyReleased(final KeyEvent event) {
    AbstractListener.scheduleSave();
  }
}
