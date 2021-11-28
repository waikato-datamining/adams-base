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
 * ImplicitClassPathAugmenter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.core.management;

import adams.core.Properties;
import adams.core.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Applies classpath augmenters listed in props file (if enabled) implicitly on startup.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImplicitClassPathAugmenter
  extends AbstractClassPathAugmenter {

  private static final long serialVersionUID = -1827690601600356255L;

  /** the properties file to load. */
  public final static String PROPERTIES_FILENAME = "adams/core/management/ImplicitClassPathAugmenter.props";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies all the classpath augmenters that are listed in the "
      + PROPERTIES_FILENAME + " props file (format: classname=<true|false>) "
      + "and have as value 'true'. This is done implicitly by the " + Launcher.class.getName() + " class.";
  }

  /**
   * Returns the classpath parts (jars, directories) to add to the classpath.
   *
   * @return the additional classpath parts
   */
  @Override
  public String[] getClassPathAugmentation() {
    List<String> 	result;
    Class		cls;
    ClassPathAugmenter	aug;
    String[]		paths;

    result = new ArrayList<>();

    for (String key: getProperties().keySetAll()) {
      if (isLoggingEnabled())
        getLogger().info("Augmenter: " + key);
      if (getProperties().getBoolean(key, false)) {
	if (isLoggingEnabled())
	  getLogger().info("Augmenter is enabled");
	try {
	  cls   = Class.forName(key);
	  aug   = (ClassPathAugmenter) cls.getDeclaredConstructor().newInstance();
	  paths = aug.getClassPathAugmentation();
	  if (isLoggingEnabled())
	    getLogger().info("Augmenter results: " + Utils.flatten(paths, ", "));
	  result.addAll(Arrays.asList(paths));
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to use classpath augmenter: " + key);
	}
      }
    }

    return result.toArray(new String[0]);
  }

  /**
   * Returns the properties with the suggestions.
   *
   * @return		the suggestions
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(PROPERTIES_FILENAME);
      }
      catch (Exception e) {
	System.err.println("Failed to read: " + PROPERTIES_FILENAME);
	e.printStackTrace();
	m_Properties = new Properties();
      }
    }
    return m_Properties;
  }
}
