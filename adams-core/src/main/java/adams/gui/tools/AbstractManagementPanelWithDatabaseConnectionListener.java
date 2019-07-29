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
 * AbstractManagementPanelWithDatabaseConnectionListener.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;

import javax.swing.SwingWorker;

/**
 * Ancestor for management panels that listen to changes of the global database
 * connection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractManagementPanelWithDatabaseConnectionListener<T extends Comparable>
  extends AbstractManagementPanelWithDatabase<T>
  implements DatabaseConnectionChangeListener {

  private static final long serialVersionUID = 2808242925776366132L;

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  @Override
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    if (e.getType() == EventType.CONNECT)
      setDatabaseConnection(e.getDatabaseConnection());

    if (e.getDatabaseConnection().isConnected()) {
      SwingWorker worker = new SwingWorker() {
	@Override
	protected Object doInBackground() throws Exception {
	  m_ButtonRefresh.setEnabled(false);
	  refresh();
	  return null;
	}
	@Override
	protected void done() {
	  m_ButtonRefresh.setEnabled(true);
	  super.done();
	}
      };
      worker.execute();
    }
    else {
      m_ModelValues.clear();
    }
  }
}
