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
 * AdditionalOptionsHandlerUtils.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.Placeholders;
import adams.core.Variables;
import adams.core.option.OptionUtils;

/**
 * Utility methods for classes implementing the AdditionalOptionsHandler
 * interface.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see AdditionalOptionsHandler
 */
public class AdditionalOptionsHandlerUtils {

  /**
   * Breaks up the option string ("key=value [key=value ...]") into key
   * value pairs and returns them in a hashtable.
   *
   * @param optStr	the option string
   * @param vars	the variables to expand
   * @return		the generated key=value relation
   * @throws Exception	if parsing of the options fails
   */
  public static AdditionalOptions breakUpOptions(String optStr, Variables vars) throws Exception {
    AdditionalOptions 	result;
    String[]		options;
    int			i;
    String		key;
    String		value;

    options = OptionUtils.splitOptions(optStr);
    result  = new AdditionalOptions();
    for (i = 0; i < options.length; i++) {
      if (options[i].indexOf("=") == -1) {
	System.err.println("Invalid option (format must be 'key=value'): " + options[i]);
	continue;
      }
      key   = options[i].substring(0, options[i].indexOf("="));
      value = options[i].substring(options[i].indexOf("=") + 1);
      // expand variables
      value = vars.expand(value);
      // expand placeholders
      value = Placeholders.getSingleton().expand(value);
      // store
      result.putString(key, value);
    }

    return result;
  }

  /**
   * Sets the (additional) options, if the provided object implements the
   * AdditionalOptionsHandler interface.
   *
   * @param obj		the object to set the options for
   * @param optStr	the options string to parse and then set
   * @param vars	the variables to expand
   * @return		true if options could be set
   * @throws Exception	if parsing of the options failed
   * @see		AdditionalOptionsHandler
   */
  public static boolean setOptions(Object obj, String optStr, Variables vars) throws Exception {
    boolean	result;

    result = false;

    if (obj instanceof AdditionalOptionsHandler) {
      ((AdditionalOptionsHandler) obj).setAdditionalOptions(breakUpOptions(optStr, vars));
      result = true;
    }

    return result;
  }
}
