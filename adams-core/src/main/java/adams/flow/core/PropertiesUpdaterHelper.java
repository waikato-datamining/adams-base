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
 * PropertiesUpdaterHelper.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.core.discovery.PropertyPath;
import adams.core.discovery.PropertyPath.PropertyContainer;

/**
 * Helper class for updating properties.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PropertiesUpdaterHelper {

  /**
   * Configures the property containers for an object.
   *
   * @param obj		the object to configure the containers for
   * @param props	the properties to configure
   * @param errors	for storing errors
   * @return		the configured containers, null if failed
   */
  public static PropertyContainer[] configure(Object obj, BaseString[] props, MessageCollection errors) {
    PropertyContainer[] result;
    int			i;
    Class		cls;

    result = new PropertyContainer[props.length];
    for (i = 0; i < props.length; i++) {
      result[i] = PropertyPath.find(obj, props[i].getValue(), errors);
      if (result[i] == null) {
	errors.add("Cannot find property '" + props[i] + "'!");
      }
      else {
	cls = result[i].getReadMethod().getReturnType();
	if (cls.isArray())
	  errors.add("Property '" + props[i] + "' is an array!");
      }
      if (!errors.isEmpty())
	return null;
    }

    return result;
  }

  /**
   * Updates the properties of the object using the current values of the variables.
   *
   * @param context 	the flow context for getting access to the variables
   * @param obj		the Java object to update
   * @param props	the properties to update
   * @param vars        the variables to use
   * @param conts	the property containers to use
   * @param errors 	for collecting errors
   * @see		#configure(Object, BaseString[], MessageCollection)
   */
  public static void update(Actor context, Object obj, BaseString[] props, VariableName[] vars, PropertyContainer[] conts, MessageCollection errors) {
    int		i;
    String	valueStr;
    Object	value;

    for (i = 0; i < props.length; i++) {
      try {
        valueStr = context.getVariables().get(vars[i].getValue());
	if (valueStr == null)
	  throw new IllegalStateException(
	      "Property #" + (i+1) + " failed to obtain variable value: " + vars[i].getValue());

	value = PropertyHelper.convertValue(conts[i], context.getVariables().get(vars[i].getValue()), errors);
	if (value == null)
	  throw new IllegalStateException(
	      "Property #" + (i+1) + " failed to convert variable value (" + vars[i].getValue() + "): " + valueStr);

	if (context.isLoggingEnabled())
	  context.getLogger().info(
	    "Updating #" + (i+1) + ": "
	      + "var=" + vars[i] + ", "
	      + "value=" + context.getVariables().get(vars[i].getValue()) + ", "
	      + "class=" + Utils.classToString(value));

	if (!PropertyPath.setValue(obj, props[i].stringValue(), value, errors)) {
	  throw new IllegalStateException(
	      "Property #" + (i+1) + " could not be updated: " + props[i].stringValue());
	}
      }
      catch (Exception e) {
	errors.add("Failed to set property '" + props[i] + "': ", e);
      }
    }
  }
}
