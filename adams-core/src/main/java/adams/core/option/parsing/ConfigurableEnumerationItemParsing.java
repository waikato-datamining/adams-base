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
 * ConfigurableEnumerationItemParsing.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option.parsing;

import adams.core.ConfigurableEnumeration.AbstractItem;
import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;

import java.lang.reflect.Array;

/**
 * For parsing ConfigurableEnumerationItem options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConfigurableEnumerationItemParsing {

  /**
   * Returns the Item as string.
   *
   * @param option	the current option
   * @param object	the Item object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((AbstractItem) object).getID();
  }

  /**
   * Returns an Item object from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Item
   * @return		the generated Item, null if failed to parse
   */
  public static Object valueOf(AbstractOption option, String str) {
    AbstractItem	defValue;
    Object		defCurrent;

    defCurrent = option.getDefaultValue();
    if (defCurrent.getClass().isArray()) {
      if (Array.getLength(defCurrent) == 0) {
        try {
	  defValue = (AbstractItem) ((AbstractArgumentOption) option).getBaseClass().newInstance();
	}
	catch (Exception e) {
          System.err.println("Failed to instantiate " + Utils.classToString(((AbstractArgumentOption) option).getBaseClass()) + "!");
          return null;
	}
      }
      else {
	defValue = (AbstractItem) Array.get(defCurrent, 0);
      }
    }
    else {
      defValue = (AbstractItem) defCurrent;
    }

    return defValue.getEnumeration().parse(str);
  }
}
