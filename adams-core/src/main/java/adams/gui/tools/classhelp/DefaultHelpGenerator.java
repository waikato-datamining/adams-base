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
 * DefaultHelpGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.classhelp;

import adams.gui.core.ConsolePanel;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Just looks for the globalInfo method.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultHelpGenerator
  extends AbstractHelpGenerator {

  /**
   * Returns whether this class is handled by this generator.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return true;
  }

  /**
   * Returns whether the generated help is HTML or plain text.
   *
   * @param cls		the class to generate the help for
   * @return		true if HTML
   */
  @Override
  public boolean isHtml(Class cls) {
    return false;
  }

  /**
   * Generates and returns the help for the specified class.
   *
   * @param cls		the class to generate the help for
   * @return		the help, null if failed to produce
   */
  @Override
  public String generateHelp(Class cls) {
    Object	obj;

    try {
      obj = cls.newInstance();
      try {
        Method method = cls.getMethod("globalInfo");
        return (String) method.invoke(obj);
      }
      catch (Exception ex2) {
        return null;
      }
    }
    catch (Exception ex) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, getClass().getName() + ": Failed to instantiate class: " + cls.getName(), ex);
    }

    return null;
  }
}
