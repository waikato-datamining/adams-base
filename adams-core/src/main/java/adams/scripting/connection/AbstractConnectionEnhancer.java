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
 * AbstractConnectionEnhancer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

/**
 * Ancestor connections that enhance a base connection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractConnectionEnhancer
  extends AbstractConnection {

  private static final long serialVersionUID = 6581951716043112610L;

  /** the connection to use. */
  protected Connection m_Connection;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "connection", "connection",
      getDefaultConnection());
  }

  /**
   * Returns the default connection to use.
   *
   * @return		the default
   */
  protected Connection getDefaultConnection() {
    return new DefaultConnection();
  }

  /**
   * Sets the connection to use.
   *
   * @param value	the connection
   */
  public void setConnections(Connection value) {
    m_Connection = value;
    reset();
  }

  /**
   * Returns the connection to use.
   *
   * @return		the connection
   */
  public Connection getConnection() {
    return m_Connection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public abstract String connectionTipText();
}
