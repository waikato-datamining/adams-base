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
 * WineUtils.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adams.core.Properties;
import adams.env.Environment;
import adams.env.WineDefinition;

/**
 * Helper class for <a href="http://www.winehq.org/" target="_blank">wine</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Wine {

  /** the properties file. */
  public final static String FILENAME = "Wine.props";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Adds, if necessary, the wine executable prefix to the commandline.
   *
   * @param cmdline	the commandline array to process
   * @param consoleApp	whether the application is a console application
   * @return		the (potentially) modified commandline
   */
  public static String[] processCommandLine(String[] cmdline, boolean consoleApp) {
    List<String>	result;
    String		executable;
    String		parameters;

    if (OS.isWindows())
      return cmdline;

    result = new ArrayList<String>(Arrays.asList(cmdline));

    if (consoleApp)
      executable = getProperties().getProperty("ConsoleExecutable", "/usr/bin/wineconsole");
    else
      executable = getProperties().getProperty("GUIExecutable", "/usr/bin/wine");

    if (consoleApp)
      parameters = getProperties().getProperty("ConsoleParameters", "");
    else
      parameters = getProperties().getProperty("GUIParameters", "");

    result.add(0, executable);
    result.add(1, parameters);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Adds, if necessary, the wine executable prefix to the commandline.
   *
   * @param cmdline	the commandline to process
   * @param consoleApp	whether the application is a console application
   * @return		the (potentially) modified commandline
   */
  public static String processCommandLine(String cmdline, boolean consoleApp) {
    String	executable;
    String	parameters;

    if (OS.isWindows())
      return cmdline;

    if (consoleApp)
      executable = getProperties().getProperty("ConsoleExecutable", "/usr/bin/wineconsole");
    else
      executable = getProperties().getProperty("GUIExecutable", "/usr/bin/wine");

    if (consoleApp)
      parameters = getProperties().getProperty("ConsoleParameters", "");
    else
      parameters = getProperties().getProperty("GUIParameters", "");

    return executable + " " + parameters + " " + cmdline;
  }

  /**
   * Loads the properties on demand.
   *
   * @return		the properties
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Environment.getInstance().read(WineDefinition.KEY);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }
}
