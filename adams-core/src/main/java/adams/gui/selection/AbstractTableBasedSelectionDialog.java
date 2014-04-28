/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * AbstractTableBasedSelectionDialog.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.lang.reflect.Array;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.gui.event.DoubleClickEvent;
import adams.gui.event.DoubleClickListener;

/**
 * Abstract ancestor for dialogs that allow the selection of items from a table.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of item that can be selected
 * @param <P> the type of selection panel
 */
public abstract class AbstractTableBasedSelectionDialog<T, P extends AbstractTableBasedSelectionPanel>
  extends AbstractSelectionDialog<T, P> {

  /** for serialization. */
  private static final long serialVersionUID = -5900377961538534246L;

  /** the chosen items. */
  protected T[] m_Current;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  protected AbstractTableBasedSelectionDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  protected AbstractTableBasedSelectionDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * initializes the GUI elements.
   */
  protected void initGUI() {
    super.initGUI();

    m_Panel = newPanel();
    m_Panel.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        update();
      }
    });
    getContentPane().add(m_Panel, BorderLayout.CENTER);

    pack();
  }

  /**
   * finishes the initialization.
   */
  protected void finishInit() {
    super.finishInit();

    m_Current = (T[]) Array.newInstance(m_Panel.getItemClass(), 0);
  }

  /**
   * Returns a new instance of the panel to use in the GUI.
   *
   * @return		the panel to use
   */
  protected abstract P newPanel();

  /**
   * Returns the underlying panel.
   *
   * @return		the panel
   */
  public P getPanel() {
    return m_Panel;
  }

  /**
   * updates the enabled state etc. of all the GUI elements.
   */
  protected void update() {
    // buttons
    m_ButtonApprove.setEnabled(m_Panel.getSelectedRowCount() >= 1);
  }

  /**
   * Sets whether multiple or single selection is used.
   *
   * @param value	if true multiple names can be selected
   */
  public void setMultipleSelection(boolean value) {
    m_Panel.setMultipleSelection(value);
  }

  /**
   * Returns whether multiple or single selection is active.
   *
   * @return		true if multiple selection is active
   */
  public boolean isMultipleSelection() {
    return m_Panel.isMultipleSelection();
  }

  /**
   * Sets the initially selected set name.
   *
   * @param value	the set name
   */
  public void setItem(T value) {
    m_Panel.setItem(value);
  }

  /**
   * Returns the set name to load, null if none chosen or dialog canceled.
   *
   * @return		the set name of the data to load
   */
  public T getItem() {
    return (T) m_Panel.getItem();
  }

  /**
   * Sets the initially selected set names.
   *
   * @param value	the set names
   */
  public void setItems(T[] value) {
    m_Current = value.clone();
    m_Panel.setItems(m_Current);
  }

  /**
   * Returns the selected set names to load, null if none chosen or dialog
   * canceled.
   *
   * @return		the set names of the data to load
   */
  public T[] getItems() {
    return m_Current;
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
    super.beforeShow();

    m_Panel.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	m_ButtonApprove.setEnabled(m_Panel.getItems().length > 0);
      }
    });
    m_Panel.addDoubleClickListener(new DoubleClickListener() {
      public void doubleClickOccurred(DoubleClickEvent e) {
	if (m_Panel.getItem() != null)
	  m_ButtonApprove.doClick();
      }
    });

    m_Panel.setItems(m_Current);
    m_Panel.scrollIntoView();
    m_Panel.grabFocus();
  }

  /**
   * Hook method just before the dialog is hidden.
   */
  protected void beforeHide() {
    super.beforeHide();

    if (m_Option == APPROVE_OPTION)
      m_Current = (T[]) m_Panel.getItems();

    cleanUp();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    m_Panel.cleanUp();
  }
}
