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
 * Drivers.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import adams.core.Properties;
import adams.core.logging.LoggingObject;
import adams.env.DriversDefinition;
import adams.env.Environment;

/**
 * Helper class for loading JDBC driver classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Drivers
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -2996917112110549746L;

  /** the filename. */
  public final static String FILENAME = "Drivers.props";

  /** the key for the drivers list in the props file. */
  public final static String KEY_DRIVERS = "Drivers";

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

    if (m_Drivers == null) {
      m_Drivers = new ArrayList();

      list = m_Properties.getProperty(KEY_DRIVERS, "").replace(" ", "").split(",");
      for (String driver: list) {
	try {
	  m_Drivers.add(Class.forName(driver).newInstance());
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
