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
 * Provenance.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.provenance;

import adams.core.Properties;
import adams.env.Environment;
import adams.env.ProvenanceDefinition;

/**
 * Singleton for accessing provenance parameters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Provenance {

  /** the name of the props file. */
  public final static String FILENAME = "Provenance.props";

  /** the singleton. */
  protected static Provenance m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** whether provenance is enabled. */
  protected Boolean m_Enabled;

  /**
   * Initializes the object.
   */
  private Provenance() {
    super();

    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Enabled = null;
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  protected synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(ProvenanceDefinition.KEY);

    return m_Properties;
  }

  /**
   * Overrides the enabled state from the props file.
   *
   * @param value	if true, provenance gets enabled
   */
  public synchronized void setEnabled(boolean value) {
    m_Enabled = value;
  }

  /**
   * Returns whether provenance is enabled.
   *
   * @return		true if provenance is enabled
   */
  public synchronized boolean isEnabled() {
    if (m_Enabled == null)
      m_Enabled = getProperties().getBoolean("Enabled", false);

    return m_Enabled;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public static synchronized Provenance getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new Provenance();

    return m_Singleton;
  }
}
