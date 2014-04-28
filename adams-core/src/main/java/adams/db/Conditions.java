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
 * Conditions.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import java.util.logging.Level;

import adams.core.Properties;
import adams.core.logging.LoggingObject;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.ConditionsDefinition;
import adams.env.Environment;

/**
 * Helper class for retrieving default conditions setups.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Conditions
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = 1697740407123800323L;

  /** the name of the props file. */
  public final static String FILENAME = "Conditions.props";

  /** the singleton instance. */
  protected static Conditions m_Singleton;

  /** the properties file. */
  protected Properties m_Properties;

  /**
   * Initializes the object.
   */
  private Conditions() {
    super();

    m_Properties = Environment.getInstance().read(ConditionsDefinition.KEY);
  }

  /**
   * Returns the default setup for the given conditions object.
   *
   * @param cond	the conditions object to return the default setup for
   * @return		the default setup
   */
  public AbstractConditions getDefault(AbstractConditions cond) {
    AbstractConditions	result;
    String		classname;
    String[]		options;

    try {
      result    = cond.shallowCopy();
      classname = cond.getClass().getName();
      if (m_Properties.hasKey(classname)) {
	options = OptionUtils.splitOptions(m_Properties.getProperty(classname));
	ArrayConsumer.setOptions(result, options);
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to get default conditions", e);
    }

    return result;
  }

  /**
   * Returns the singleton instance. Instantiates it if necessary.
   *
   * @return		the singleton instance
   */
  public static synchronized Conditions getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new Conditions();

    return m_Singleton;
  }
}
