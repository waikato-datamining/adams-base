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
 * EnumHelpGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.classhelp;

import adams.core.EnumHelper;
import adams.core.EnumWithCustomDisplay;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 * Handler for enums.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EnumHelpGenerator
  extends AbstractHelpGenerator {

  /**
   * Returns whether this class is handled by this generator.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(EnumWithCustomDisplay.class, cls)
      || ClassLocator.isSubclass(Enum.class, cls);
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
    StringBuilder	result;
    Object[]		values;
    boolean		display;

    result  = new StringBuilder();
    values  = EnumHelper.getValues(cls);
    display = ClassLocator.hasInterface(EnumWithCustomDisplay.class, cls);

    for (Object value: values) {
      if (display)
	result.append(((EnumWithCustomDisplay) value).toRaw()).append("/");
      result.append(value.toString());
      result.append("\n");
    }

    return result.toString();
  }
}
