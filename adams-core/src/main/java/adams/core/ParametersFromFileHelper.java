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
 * ParametersFromFileHelper.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.logging.Logger;
import adams.core.logging.LoggingSupporter;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionManager;
import adams.core.option.OptionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * Helper class for loading and applying parameters to classes implementing
 * {@link ParametersFromFileSupporter}.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @see ParametersFromFileSupporter
 */
public class ParametersFromFileHelper {

  /**
   * Returns the tool tip to use for the parameters file.
   *
   * @return		the tool tip
   */
  public static String parametersFileTipText() {
    return "The Java properties file containing the parameters and their associated values to apply. "
	     + "The properties in the file must align with the Bean properties/ADAMS option of the object that is "
	     + "to be updated. If the option takes an array, then the values for the array must be blank-separated.";
  }

  /**
   * Loads and applies the parameters, if possible.
   *
   * @param supporter	the object to update
   * @return		null if successful or nothing to do, otherwise error message
   */
  public static String applyParameters(ParametersFromFileSupporter supporter) {
    Properties			props;
    OptionManager		manager;
    Logger			logger;
    AbstractOption		option;
    AbstractArgumentOption	arg;
    Method			method;
    String			msg;
    String			value;
    String[]			parts;
    Object			objects;
    int				i;

    if (!supporter.getParametersFile().exists())
      return "Parameters file does not exist: " + supporter.getParametersFile();
    // directory? nothing to do
    if (supporter.getParametersFile().isDirectory())
      return null;

    props = new Properties();
    if (!props.load(supporter.getParametersFile().getAbsolutePath()))
      return "Failed to load parameters from props file: " + supporter.getParametersFile();

    // apply the parameters
    manager = supporter.getOptionManager();
    logger  = null;
    if (supporter instanceof LoggingSupporter)
      logger = ((LoggingSupporter) supporter).getLogger();
    for (String property : props.keySetAll()) {
      option = manager.findByProperty(property);
      if (option != null) {
	if (option instanceof AbstractArgumentOption) {
	  arg    = (AbstractArgumentOption) option;
	  value  = props.getProperty(property);
	  method = arg.getDescriptor().getWriteMethod();
	  try {
	    if (arg.isMultiple()) {
	      parts   = OptionUtils.splitOptions(value);
	      objects = Array.newInstance(arg.getBaseClass(), parts.length);
	      for (i = 0; i < parts.length; i++)
		Array.set(objects, i, ((AbstractArgumentOption) option).valueOf(parts[i]));
	    }
	    else {
	      objects = ((AbstractArgumentOption) option).valueOf(value);
	    }
	    method.invoke(option.getOptionHandler(), objects);
	  }
	  catch (Exception e) {
	    msg = "Failed to parse/set value for option '" + property + "': " + value;
	    if (logger != null)
	      logger.severe(msg, e);
	    return msg + "\n" + e;
	  }
	}
	else {
	  if (logger != null)
	    logger.warning("Option does not take an argument: " + property);
	}
      }
      else {
	if (logger != null)
	  logger.warning("Unknown option: " + property);
      }
    }

    return null;
  }
}
