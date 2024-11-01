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
 * Drivers.java
 * Copyright (C) 2011-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.core.Properties;
import adams.core.classmanager.ClassManager;
import adams.core.logging.LoggingObject;
import adams.env.DriversDefinition;
import adams.env.Environment;

import java.util.*;
import java.util.logging.Level;

/**
 * Helper class for loading JDBC driver classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Drivers
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -2996917112110549746L;

  /** the filename. */
  public final static String FILENAME = "Drivers.props";

  /** the key for the drivers list in the props file. */
  public final static String KEY_DRIVERS = "Drivers";

  /** the key for the excluded drivers list in the props file. */
  public final static String KEY_EXCLUDED = "Excluded";

  /** the singleton. */
  protected static Drivers m_Singleton;

  /** the properties for database access. */
  protected Properties m_Properties;

  /** The JDBC drivers. */
  protected transient List m_Drivers;

  /**
   * Initializes the object.
   */
  private Drivers() {
    super();

    m_Properties = Environment.getInstance().read(DriversDefinition.KEY);
    initialize();
  }
  
  /**
   * Initializes drivers and data types.
   */
  protected void initialize() {
    getDrivers();
  }

  /**
   * Returns the vector with drivers. Initializes it if necessary.
   *
   * @return		the drivers
   */
  public List getDrivers() {
    String[]	list;
    Set<String> excluded;

    if (m_Drivers == null) {
      m_Drivers = new ArrayList();

      list     = m_Properties.getProperty(KEY_DRIVERS, "").replace(" ", "").split(",");
      excluded = new HashSet<>(Arrays.asList(m_Properties.getProperty(KEY_EXCLUDED, "").replace(" ", "").split(",")));
      for (String driver: list) {
        if (excluded.contains(driver)) {
          getLogger().info("Excluded driver: " + driver);
          continue;
        }
	try {
	  getLogger().severe("Adding driver: " + driver);
	  m_Drivers.add(ClassManager.getSingleton().forName(driver).getDeclaredConstructor().newInstance());
	}
	catch(Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to load driver '" + driver + "': ", e);
	}
      }
    }

    return m_Drivers;
  }
  
  /**
   * Returns the singleton, instantiates it if necessary.
   *
   * @return		the singleton
   */
  public static synchronized Drivers getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new Drivers();

    return m_Singleton;
  }
}
