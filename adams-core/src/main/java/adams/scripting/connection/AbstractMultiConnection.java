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
  protected Connection[] m_Connections;

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
   * Sets the connections to manage.
   *
   * @param value	the connections
   */
  public void setConnections(Connection[] value) {
    m_Connections = value;
    reset();
  }

  /**
   * Returns the connections to manage.
   *
   * @return		the connections
   */
  public Connection[] getConnections() {
    return m_Connections;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public abstract String connectionsTipText();
}
