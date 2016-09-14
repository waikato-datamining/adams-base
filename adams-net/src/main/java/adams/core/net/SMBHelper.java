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
 * SMBHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net;

import adams.core.Properties;
import adams.env.Environment;
import adams.env.SMBDefinition;

/**
 * A helper class for SMB.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SMBHelper {

  /** the name of the props file. */
  public final static String FILENAME = "SMB.props";

  /** The wins host. */
  public final static String WINS_HOST = "WinsHost";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns the underlying properties.
   *
   * @return		the properties
   */
  public synchronized static Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Environment.getInstance().read(SMBDefinition.KEY);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }

  /**
   * Writes the specified properties to disk.
   *
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties() {
    return writeProperties(getProperties());
  }

  /**
   * Writes the specified properties to disk.
   *
   * @param props	the properties to write to disk
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties(Properties props) {
    boolean	result;

    result = Environment.getInstance().write(SMBDefinition.KEY, props);
    // require reload
    m_Properties = null;

    return result;
  }

  /**
   * Returns the hostname verifier.
   *
   * @return		the verifier
   */
  public static String getWinsHost() {
    return getProperties().getProperty(WINS_HOST, "192.168.1.220");
  }

  /**
   * Initializes the Wins host.
   */
  public static synchronized void initialize() {
    jcifs.Config.setProperty("jcifs.netbios.wins", getWinsHost());
  }
}
