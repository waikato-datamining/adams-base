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

/**
 * AbstractDatabaseSelectionDialog.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.selection;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;

/**
 * Abstract ancestor for dialogs that allow the selection of items from a table,
 * using a database as backend.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of item that can be selected
 * @param <P> the type of selection panel
 */
public abstract class AbstractDatabaseSelectionDialog<T, P extends AbstractDatabaseSelectionPanel>
  extends AbstractTableBasedSelectionDialog<T,P>
  implements DatabaseConnectionChangeListener, DatabaseConnectionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4600292804155253498L;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  protected AbstractDatabaseSelectionDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  protected AbstractDatabaseSelectionDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Adds the given listener to the Refresh button.
   *
   * @param l		the listener to add
   */
  public void addRefreshActionListener(ActionListener l) {
    m_Panel.addRefreshActionListener(l);
  }

  /**
   * Removes the given listener from the Refresh button.
   *
   * @param l		the listener to remove
   */
  public void removeRefreshActionListener(ActionListener l) {
    m_Panel.removeRefreshActionListener(l);
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  @Override
  protected void beforeShow() {
    if (getDatabaseConnection() != null) {
      getDatabaseConnection().addChangeListener(m_Panel);
      getDatabaseConnection().addChangeListener(this);
    }

    super.beforeShow();
  }

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    m_Current = (T[]) Array.newInstance(m_Panel.getItemClass(), 0);
    if (e.getType() == EventType.CONNECT)
      setDatabaseConnection(e.getDatabaseConnection());
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public abstract AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public abstract void setDatabaseConnection(AbstractDatabaseConnection value);

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (getDatabaseConnection() != null) {
      getDatabaseConnection().removeChangeListener(m_Panel);
      getDatabaseConnection().removeChangeListener(this);
    }
    super.cleanUp();
  }
}
