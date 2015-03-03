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
 * QuickInfoHelper.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.List;

import adams.core.base.BaseRegExp;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionConsumer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionProducer;
import adams.gui.core.ColorHelper;

/**
 * Helper class for assembling quick info strings returned by classes 
 * implementing {@link QuickInfoSupporter}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class QuickInfoHelper {

  /**
   * Checks whether a variable is attached to the optionhandler's property.
   * 
   * @param handler	the option handler to inspect
   * @param property	the property of the option handler to inspect
   * @return		true if a variable is attached
   */
  public static boolean hasVariable(OptionHandler handler, String property) {
    return (getVariable(handler, property) != null);
  }

  /**
   * Returns the variable attached to optionhandler's property.
   * 
   * @param handler	the option handler to inspect
   * @param property	the property of the option handler to inspect
   * @return		the variable (incl @{...}), if attached, otherwise null
   */
  public static String getVariable(OptionHandler handler, String property) {
    String		result;
    AbstractOption	option;
    
    result = handler.getOptionManager().getVariableForProperty(property);
    if (result != null) {
      option = handler.getOptionManager().findByProperty(property);
      if (option instanceof AbstractArgumentOption) {
	if (ClassLocator.isSubclass(VariableName.class, ((AbstractArgumentOption) option).getBaseClass()))
	  result = Variables.START + result + Variables.END;
      }
    }
    
    return result;
  }

  /**
   * Returns either the variable name attached to optionhandler's property or
   * the current value as string.
   * 
   * @param handler	the option handler to inspect
   * @param property	the property of the option handler to inspect
   * @param current	the current value
   * @return		the generated string, can be null if current is null or empty string
   */
  public static String toString(OptionHandler handler, String property, Object current) {
    return toString(handler, property, current, null);
  }

  /**
   * Generates a string representation from the object (non-array).
   * 
   * @param current	the object to turn into a string
   * @return		the generated string
   */
  protected static String toString(Object current) {
    String	result;
    
    if (current instanceof String) {
      if (((String) current).length() == 0)
	result = null;
      else
	result = (String) current;
    }
    else if (current instanceof Range) {
      result = ((Range) current).getRange();
    }
    else if (current instanceof Index) {
      result = ((Index) current).getIndex();
    }
    else if (current instanceof OptionHandler) {
      result = current.getClass().getSimpleName();
    }
    else if (current instanceof EnumWithCustomDisplay) {
      result = ((EnumWithCustomDisplay) current).toDisplay();
    }
    else if (current instanceof Class) {
      result = ((Class) current).getSimpleName();
    }
    else if (current instanceof OptionProducer) {
      result = current.getClass().getSimpleName();
    }
    else if (current instanceof OptionConsumer) {
      result = current.getClass().getSimpleName();
    }
    else if (current instanceof Color) {
      result = ColorHelper.toHex((Color) current);
    }
    else {
      result = current.toString();
    }
    
    return result;
  }
  
  /**
   * Returns either the variable name attached to optionhandler's property or
   * the current value as string. <p/>
   * Special handling:
   * If the object is an instance of {@link QuickInfoSupporter} the simple 
   * class name and the quick info in parentheses (if applicable) is output.
   * {@link Range} and {@link Index} automatically have the string output.
   * For {@link OptionHandler} classes, only the simple class name is output.
   * For {@link Class} objects, the simple class name is output.
   * For {@link EnumWithCustomDisplay} enums, the display string is output.
   * 
   * @param handler	the option handler to inspect
   * @param property	the property of the option handler to inspect
   * @param current	the current value
   * @param prefix	the prefix string to prepend the variable/text with, ignored if null
   * @return		the generated string, can be null if current is null or empty string
   */
  public static String toString(OptionHandler handler, String property, Object current, String prefix) {
    String	result;
    String	variable;
    String	info;
    int		dim;
    int		i;
    
    variable = getVariable(handler, property);
    if (variable != null) {
      result = variable;
    }
    else if (current == null) {
      result = null;
    }
    else {
      if (current instanceof QuickInfoSupporter) {
	info = ((QuickInfoSupporter) current).getQuickInfo();
	result = current.getClass().getSimpleName();
	if (info != null)
	  result += " (" + info + ")";
      }
      else if (current.getClass().isArray()) {
	dim = Utils.getArrayDimensions(current);
	if (dim > 1) {
	  result = current.getClass().getSimpleName();
	  for (i = 1; i < dim; i++)
	    result += "[]";
	}
	else {
	  result = "";
	  for (i = 0; i < Array.getLength(current); i++) {
	    if (result.length() > 0)
	      result += "|";
	    result += toString(Array.get(current, i));
	  }
	}
      }
      else {
	result = toString(current);
      }
    }

    if ((result != null) && (prefix != null))
      result = prefix + result;
    
    return result;
  }

  /**
   * Returns either the variable name attached to optionhandler's property or
   * the current value as string.
   * 
   * @param handler	the option handler to inspect
   * @param property	the property of the option handler to inspect
   * @param current	the current value
   * @return		the generated string, null if no variable or empty regexp
   */
  public static String toString(OptionHandler handler, String property, BaseRegExp current) {
    return toString(handler, property, current, null);
  }

  /**
   * Returns either the variable name attached to optionhandler's property or
   * the current value as string.
   * 
   * @param handler	the option handler to inspect
   * @param property	the property of the option handler to inspect
   * @param current	the current value
   * @param prefix	the prefix string to prepend the variable/text with, ignored if null
   * @return		the generated string, null if no variable or empty regexp
   */
  public static String toString(OptionHandler handler, String property, BaseRegExp current, String prefix) {
    return toString(handler, property, current, null, prefix);
  }

  /**
   * Returns either the variable name attached to optionhandler's property or
   * the current value as string.
   * 
   * @param handler	the option handler to inspect
   * @param property	the property of the option handler to inspect
   * @param current	the current value
   * @param ifEmpty	the string to output if the regexp is empty, null to ignore
   * @param prefix	the prefix string to prepend the variable/text with, ignored if null
   * @return		the generated string, null if no variable or empty regexp
   */
  public static String toString(OptionHandler handler, String property, BaseRegExp current, String ifEmpty, String prefix) {
    String	result;
    String	variable;
    
    result = null;
    
    variable = getVariable(handler, property);
    if (variable != null)
      result = variable;
    else if (current == null)
      result = null;
    else if (!current.isEmpty())
      result = current.stringValue();
    else if (ifEmpty != null)
      result = ifEmpty;
    
    if ((result != null) && (prefix != null))
      result = prefix + result;
    
    return result;
  }

  /**
   * Returns either the variable name attached to optionhandler's property or
   * the text if the current value is true. In both cases, the prefix (if not null)
   * is prepended to the result string.
   * 
   * @param handler	the option handler to inspect
   * @param property	the property of the option handler to inspect
   * @param current	the current value
   * @param text	the text to output if no variable is set
   * @return		the generated string
   */
  public static String toString(OptionHandler handler, String property, boolean current, String text) {
    return toString(handler, property, current, text, null);
  }

  /**
   * Returns either the variable name attached to optionhandler's property or
   * the text if the current value is true. In both cases, the prefix (if not null)
   * is prepended to the result string.
   * 
   * @param handler	the option handler to inspect
   * @param property	the property of the option handler to inspect
   * @param current	the current value
   * @param text	the text to output if no variable is set
   * @param prefix	the prefix string to prepend the variable/text with, ignored if null
   * @return		the generated string
   */
  public static String toString(OptionHandler handler, String property, boolean current, String text, String prefix) {
    String	result;
    String	variable;
    
    result = "";
    
    variable = getVariable(handler, property);
    if (variable != null) {
      if (prefix != null)
	result += prefix;
      result += variable;
    }
    else if (current) {
      if (prefix != null)
	result += prefix;
      result += text;
    }
    
    return result;
  }
  
  /**
   * Adds the value to the list if neither null nor empty string.
   * 
   * @param list	the list to add the value to
   * @param value	the value to add
   * @return		true if added
   */
  public static boolean add(List<String> list, String value) {
    if (value == null)
      return false;
    if (value.length() == 0)
      return false;
    list.add(value);
    return true;
  }
  
  /**
   * Returns the flattened list with a blank prepended and in brackets if
   * at least one item in the list. Otherwise an empty string gets returned.
   * 
   * @param list	the list of flags/options to turn into a string
   * @return		the generated list
   */
  public static String flatten(List<String> list) {
    if (list.size() > 0)
      return " [" + Utils.flatten(list, ", ") + "]";
    else
      return "";
  }
}
