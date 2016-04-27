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
 * AbstractMultiConnection.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for connection classes that manage multiple base connections.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiConnection
  extends AbstractConnection {

  private static final long serialVersionUID = 6581951716043112610L;

  /** the connections to manage. */
  protected List<Connection> m_Connections;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "connection", "connections",
      new Connection[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Connections = new ArrayList<>();
  }

  /**
   * Sets the connections to manage.
   *
   * @param value	the connections
   */
  public void setConnections(Connection[] value) {
    m_Connections.clear();
    Collections.addAll(m_Connections, value);
    reset();
  }

  /**
   * Returns the connections to manage.
   *
   * @return		the connections
   */
  public Connection[] getConnections() {
    return m_Connections.toArray(new Connection[m_Connections.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public abstract String connectionsTipText();

  /**
   * Allows adding a connecction at runtime, without triggering a reset.
   *
   * @param conn	the connection to add
   */
  public void addConnection(Connection conn) {
    m_Connections.add(conn);
  }

  /**
   * Allows removing a connecction at runtime, without triggering a reset.
   *
   * @param conn	the connection to remove
   */
  public void removeConnection(Connection conn) {
    m_Connections.remove(conn);
  }
}
