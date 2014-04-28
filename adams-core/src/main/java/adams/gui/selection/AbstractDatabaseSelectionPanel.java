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
 * AbstractDatabaseSelectionPanel.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.selection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;

/**
 * Abstract ancestor for table-based selection panels that use the database
 * as backend.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of items to display
 */
public abstract class AbstractDatabaseSelectionPanel<T>
  extends AbstractTableBasedSelectionPanel<T>
  implements DatabaseConnectionChangeListener, DatabaseConnectionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3870916380764854145L;

  /** the button for refreshing the fields. */
  protected JButton m_ButtonRefresh;

  /** whether data has been displayed already. */
  protected boolean m_DataDisplayed;

  /** the database connection in use. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_DataDisplayed      = false;
    m_DatabaseConnection = getDefaultDatabaseConnection().getClone();
    m_DatabaseConnection.addChangeListener(this);
  }

  /**
   * Initializes the GUI elements.
   */
  protected void initGUI() {
    super.initGUI();

    m_ButtonRefresh = new JButton("Refresh");
    m_ButtonRefresh.setMnemonic('R');
    m_ButtonRefresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	refresh();
      }
    });
    m_SearchPanel.addToWidgetsPanel(m_ButtonRefresh);
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Sets the enabled state of the panel.
   *
   * @param value	if true then the components will be enabled
   */
  public void setEnabled(boolean value) {
    super.setEnabled(value);

    m_ButtonRefresh.setEnabled(value);
  }

  /**
   * Simulates a click on the refresh button, if necessary.
   *
   * @see	#refresh()
   */
  public abstract void refreshIfNecessary();

  /**
   * Performs the actual refresh.
   */
  protected abstract void doRefresh();

  /**
   * Refreshes the items.
   */
  public void refresh() {
    refresh(getCurrentItems().clone());
  }

  /**
   * Refreshes the items.
   */
  protected void refresh(final T[] items) {
    Runnable	run;

    run = new Runnable() {
      public void run() {
	m_ButtonRefresh.setEnabled(false);
	doRefresh();
	m_Current = (T[]) select(items).toArray();
	updateCounts();
	m_ButtonRefresh.setEnabled(true);
	m_DataDisplayed = true;
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Sets the initially selected set names.
   *
   * @param value	the set names to select
   */
  public void setItems(T[] value) {
    int		i;

    // currently refreshing?
    if (!waitForEnabled(m_ButtonRefresh)) {
      m_Current = (T[]) Array.newInstance(getItemClass(), value.length);
      for (i = 0; i < value.length; i++)
	m_Current[i] = value[i];
    }
    else {
      if (!m_DataDisplayed)
	refresh(value);
      else
	super.setItems(value);
    }
  }

  /**
   * closes/shows the dialog.
   *
   * @param value	if true then display the dialog, otherwise close it
   */
  public void setVisible(boolean value) {
    if (value && !m_DataDisplayed)
      refresh();

    super.setVisible(value);
  }

  /**
   * Adds the given listener to the Refresh button.
   *
   * @param l		the listener to add
   */
  public void addRefreshActionListener(ActionListener l) {
    m_ButtonRefresh.addActionListener(l);
  }

  /**
   * Removes the given listener from the Refresh button.
   *
   * @param l		the listener to remove
   */
  public void removeRefreshActionListener(ActionListener l) {
    m_ButtonRefresh.removeActionListener(l);
  }

  /**
   * Gets called when the database connection gets established.
   */
  protected void databaseConnected() {
    refresh();
  }

  /**
   * Gets called when the database connection gets disconnected.
   */
  protected abstract void databaseDisconnected();

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    m_Current = (T[]) Array.newInstance(getItemClass(), 0);
    if (e.getType() == EventType.CONNECT) {
      setDatabaseConnection(e.getDatabaseConnection());
      databaseConnected();
    }
    else {
      databaseDisconnected();
    }
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    if (value == null)
      value = getDefaultDatabaseConnection();

    if (!m_DatabaseConnection.equals(value)) {
      m_DataDisplayed = false;

      m_DatabaseConnection.removeChangeListener(this);
      m_DatabaseConnection = value.getClone();
      m_DatabaseConnection.addChangeListener(this);
      if (m_DatabaseConnection.isConnected())
	databaseConnected();
      else
	databaseDisconnected();
    }
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
    super.beforeShow();
    m_ButtonRefresh.setEnabled(m_DatabaseConnection.isConnected());
    if (m_DatabaseConnection.isConnected())
      refreshIfNecessary();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_DatabaseConnection.removeChangeListener(this);
    super.cleanUp();
  }
}
