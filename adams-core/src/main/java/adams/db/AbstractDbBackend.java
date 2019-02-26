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
 * AbstractDbBackend.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.ClassLister;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.logging.LoggingLevel;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.env.DbBackendDefinition;
import adams.env.Environment;

/**
 * Ancestor for classes that provide a processing backend.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDbBackend
  extends AbstractOptionHandler
  implements DbBackend {

  private static final long serialVersionUID = 3589560540375200811L;

  public static final String KEY_BACKEND = "Backend";

  /** the properties with the defaults. */
  protected static Properties m_Properties;

  /** the singleton. */
  protected static DbBackend m_Singleton;

  /** whether the backend was initialized. */
  protected static boolean m_Initialized;

  /**
   * Returns the underlying properties.
   *
   * @return		the properties
   */
  protected synchronized static Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(DbBackendDefinition.KEY);
    return m_Properties;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public static synchronized DbBackend getSingleton() {
    String	cmdline;
    Class[]	available;

    if (m_Singleton == null) {
      if (!m_Initialized) {
        available = ClassLister.getSingleton().getClasses(DbBackend.class);
	m_Initialized = true;
	cmdline = getProperties().getProperty(KEY_BACKEND, "").trim();
	if (cmdline.isEmpty())
	  throw new IllegalStateException(
	    "No DB backend defined in " + DbBackend.FILENAME + "!\n"
	      + "Available: " + Utils.classesToString(available));
	try {
	  m_Singleton = (DbBackend) OptionUtils.forCommandLine(DbBackend.class, cmdline);
	  m_Singleton.setLoggingLevel(LoggingLevel.INFO);
	  m_Singleton.getLogger().info("Using DB Backend: " + m_Singleton.toCommandLine());
	}
	catch (Exception e) {
	  m_Singleton = null;
	  throw new IllegalStateException(
	    "Failed to instantiate DB backend (" + DbBackend.FILENAME + "): " + cmdline + "\n"
	      + "Available: " + Utils.classesToString(available));
	}
      }
    }

    return m_Singleton;
  }
}
