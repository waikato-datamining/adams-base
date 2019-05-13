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
 * SimpleScriptParsing.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option.parsing;

import adams.core.Utils;
import adams.core.option.AbstractOption;
import adams.gui.core.AbstractSimpleScript;

/**
 * For parsing AbstractSimpleScript derived options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleScriptParsing {

  /**
   * Returns the script as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((AbstractSimpleScript) object).stringValue();
  }

  /**
   * Returns a script object generated from the string.
   *
   * @param cls		the script class
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(Class cls, String str) {
    AbstractSimpleScript	result;

    try {
      if (cls.isArray())
	cls = cls.getComponentType();
      result = (AbstractSimpleScript) cls.newInstance();
      result.setValue(Utils.unbackQuoteChars(str));
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Returns a script object generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return valueOf(option.getDefaultValue().getClass(), str);
  }
}