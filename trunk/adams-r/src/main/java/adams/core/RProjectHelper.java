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
 * RProjectHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.File;

import adams.env.Environment;
import adams.env.RProjectDefinition;

/**
 * Helper class for the R project setup.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RProjectHelper {

  /** the props file. */
  public final static String FILENAME = "RProject.props";

  /** the R executable. */
  public final static String R_EXECUTABLE = "RExecutable";

  /** the Rserve host to connect to. */
  public final static String RSERVE_HOST = "RserveHost";

  /** the Rserve port to connect on. */
  public final static String RSERVE_PORT = "RservePort";

  /** the singleton. */
  protected static RProjectHelper m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** whether the settings got modified. */
  protected boolean m_Modified;

  /**
   * Initializes the helper.
   */
  private RProjectHelper() {
    super();
    reload();
  }

  /**
   * Whether the settings got modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Returns the R executable.
   *
   * @return		the executable
   */
  public File getRExecutable() {
    return new File(m_Properties.getPath(R_EXECUTABLE, "R"));
  }

  /**
   * Updates the R executable.
   *
   * @param value	the executable
   */
  public void setRExecutable(File value) {
    m_Modified = true;
    m_Properties.setProperty(R_EXECUTABLE, value.getAbsolutePath());
  }

  /**
   * Returns the Rserve host.
   *
   * @return		the host
   */
  public String getRserveHost() {
    return m_Properties.getPath(RSERVE_HOST, "localhost");
  }

  /**
   * Updates the Rserve host.
   *
   * @param value	the host
   */
  public void setRserveHost(String value) {
    m_Modified = true;
    m_Properties.setProperty(RSERVE_HOST, value);
  }

  /**
   * Returns the Rserve port.
   *
   * @return		the port
   */
  public int getRservePort() {
    return m_Properties.getInteger(RSERVE_PORT, 6311);
  }

  /**
   * Updates the Rserve port.
   *
   * @param value	the port
   */
  public void setRservePort(int value) {
    m_Modified = true;
    m_Properties.setInteger(RSERVE_PORT, value);
  }

  /**
   * Reloads the properties file. Discards any unsaved settings.
   */
  public synchronized void reload() {
    m_Modified = false;

    try {
      m_Properties = Environment.getInstance().read(RProjectDefinition.KEY);
    }
    catch (Exception e) {
      m_Properties = new Properties();
    }
  }

  /**
   * Saves the settings in the user's home directory.
   *
   * @return		true if successfully saved
   */
  public synchronized boolean save() {
    boolean	result;

    result = Environment.getInstance().write(RProjectDefinition.KEY, m_Properties);
    if (result)
      m_Modified = false;

    return result;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public synchronized static RProjectHelper getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new RProjectHelper();

    return m_Singleton;
  }
}
