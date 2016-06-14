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
 * AbstractArgumentOption.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.Utils;
import adams.core.Variables;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeListener;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The ancestor of all option classes that take an argument.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractArgumentOption
  extends AbstractOption
  implements VariableChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = 8495236290921805608L;

  /** the base class. */
  protected Class m_BaseClass;

  /** indicates whether the option can appear multiple times (for array
   * properties). */
  protected boolean m_Multiple;

  /** the variable placeholder. */
  protected String m_Variable;

  /** whether the variable got updated and needs propagating. */
  protected boolean m_VariableModified;

  /** whether the variable references a global actor or storage. */
  protected Boolean m_VariableReferencesObject;

  /**
   * Initializes the option. Always outputs the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected AbstractArgumentOption(OptionManager owner, String commandline, String property,
      Object defValue) {

    this(owner, commandline, property, defValue, true);
  }

  /**
   * Initializes the option.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   * @param outputDefValue	whether to output the default value or not
   */
  protected AbstractArgumentOption(OptionManager owner, String commandline, String property,
      Object defValue, boolean outputDefValue) {

    super(owner, commandline, property, defValue, outputDefValue);

    m_Variable                  = null;
    m_VariableModified          = false;
    m_VariableReferencesObject  = null;
    m_BaseClass                 = null;
    m_Multiple                  = false;

    // determine the base class
    if (getDescriptor() != null) {
      // determine whether option can be specified multiple times, i.e., get/set
      // methods return/take an array
      m_Multiple = getDescriptor().getReadMethod().getReturnType().isArray();

      if (isMultiple())
	m_BaseClass = getDescriptor().getPropertyType().getComponentType();
      else
	m_BaseClass = getDescriptor().getPropertyType();
    }
  }

  /**
   * Returns true if the option can appear multiple times (for array properties).
   *
   * @return		true if the option can appear multiple times.
   */
  public boolean isMultiple() {
    return m_Multiple;
  }

  /**
   * Returns whether a base class could be determined.
   *
   * @return		true if a base class is available
   */
  public boolean hasBaseClass() {
    return (m_BaseClass != null);
  }

  /**
   * Returns the base class of this option. In case of arrays the class of
   * the elements.
   *
   * @return		the class
   */
  public Class getBaseClass() {
    return m_BaseClass;
  }

  /**
   * Whether a variable has been attached to the option.
   *
   * @return		true if a variable placeholder has been set
   */
  public boolean isVariableAttached() {
    return (m_Variable != null);
  }

  /**
   * Sets the variable placeholder to use, or null to disable.
   *
   * @param value	the variable placeholder
   */
  public void setVariable(String value) {
    m_VariableReferencesObject = null;
    if (value != null)
      m_Variable = Variables.extractName(value);
    else
      m_Variable = null;
  }

  /**
   * Returns the variable placeholder, if any.
   *
   * @return		the variable placeholder, or null if none set
   */
  public String getVariable() {
    if (m_Variable == null)
      return null;
    else
      return Variables.padName(m_Variable);
  }

  /**
   * Returns the variable name (without the surrounding "@{" and "}"), if any.
   *
   * @return		the variable placeholder, or null if none set
   */
  public String getVariableName() {
    return m_Variable;
  }

  /**
   * Returns whether the variable got updated in the meantime and needs
   * propagating.
   *
   * @return		true if the variable got updated
   */
  public boolean isVariableModified() {
    return isVariableAttached() && m_VariableModified;
  }

  /**
   * Returns whether the variable points to a global actor or storage.
   *
   * @return		true if the variable references a global actor or storage
   */
  public boolean isVariableReferencingObject() {
    if (m_Variable == null)
      return false;
    if (m_VariableReferencesObject == null)
      m_VariableReferencesObject = getOwner().getVariables().isObject(m_Variable);
    return m_VariableReferencesObject;
  }

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    if (!isVariableAttached())
      return;

    if (e.getName().equals(getVariableName()))
      m_VariableModified = true;
  }

  /**
   * Updates the variable, i.e., retrieves the value for the variable
   * and calls the read-method of this option to set it.
   *
   * @return		null if successfully updated, otherwise error message
   */
  public String updateVariable() {
    return updateVariable(false);
  }

  /**
   * Updates the variable, i.e., retrieves the value for the variable
   * and calls the read-method of this option to set it.
   *
   * @return		null if successfully updated, otherwise error message
   */
  public String updateVariable(boolean silent) {
    Method	method;
    Variables	vars;
    Object	value;
    String[]	values;
    Object	objects;
    int		i;

    method = getWriteMethod();
    if (method == null) {
      if (!m_Owner.isQuiet())
	System.err.println(
	    "Failed to obtain write method for option '" + getCommandline() + "/" + getProperty()
	    + ", cannot set variable value ('" + m_Variable + "')!");
      return "Write method not found";
    }

    vars = getOwner().getVariables();
    if (!vars.has(m_Variable)) {
      if (!m_Owner.isQuiet() && !silent)
	System.err.println("Variable '" + m_Variable + "' is not defined (" + getOwner().getVariables().hashCode() + ")!");
      return "Variable '" + m_Variable + "' not defined (" + getOwner().getVariables().hashCode() + ")";
    }

    if (vars.isObject(m_Variable))
      value = vars.getObject(m_Variable);
    else
      value = vars.get(m_Variable);
    if (value == null) {
      if (!m_Owner.isQuiet() && !silent)
	System.err.println("Variable '" + m_Variable + "' has no value associated!");
      return "Variable '" + m_Variable + "' has no value associated";
    }

    if (vars.isObject(m_Variable)) {
      try {
	method.invoke(
	    getOptionHandler(),
	    new Object[]{value});
	m_VariableModified = false;
	return null;
      }
      catch (Exception e) {
	if (!m_Owner.isQuiet() && !silent) {
	  System.err.println(
	      "Failed to set value for variable '" + m_Variable + "' (" + value.getClass().getName() + "): " + value);
	  e.printStackTrace();
	  System.err.println("Wrong class? Attempting to set value using string representation instead!");
	}
	value = vars.get(m_Variable);
      }
    }

    try {
      if (isMultiple()) {
	values  = OptionUtils.splitOptions((String) value);
	objects = Array.newInstance(getBaseClass(), values.length);
	for (i = 0; i < values.length; i++)
	  Array.set(objects, i, valueOf(values[i]));
	value = objects;
      }
      else {
	value = valueOf((String) value);
      }
      method.invoke(
	  getOptionHandler(),
	  new Object[]{value});
      m_VariableModified = false;
    }
    catch (Exception e) {
      if (!m_Owner.isQuiet()) {
	if (value.getClass().isArray())
	  System.err.println(
	      "Failed to set value for variable '" + m_Variable + "' (" + Utils.getArrayClass(value.getClass()).getName() + "): " + value);
	else
	  System.err.println(
	      "Failed to set value for variable '" + m_Variable + "' (" + value.getClass().getName() + "): " + value);
	e.printStackTrace();
      }
      return "Failed to set value for variable '" + m_Variable + "'";
    }

    return null;
  }

  /**
   * Turns the string into the appropriate object.
   *
   * @param s		the string to parse
   * @return		the generated object
   * @throws Exception	if parsing of string fails
   */
  public abstract Object valueOf(String s) throws Exception;

  /**
   * Returns a string representation of the specified object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public abstract String toString(Object obj);

  /**
   * Compares the two values.
   *
   * @param value	the value to compare against the default value
   * @param defValue	the default value to compare against
   * @return		true if both are equal
   */
  protected abstract boolean compareValues(Object value, Object defValue);

  /**
   * Compares the given value against the default value.
   *
   * @param value	the value to compare against the default value
   * @return		true if the value is the same as the default value
   */
  protected boolean isDefaultValue(Object value) {
    boolean	result;
    int		len;
    int		i;

    // should default values be suppressed?
    if (!OptionUtils.getSuppressDefaultValues()) {
      result = false;
    }
    else {
      result = true;
      if (isMultiple()) {
	result = (Array.getLength(value) == Array.getLength(getDefaultValue()));
	if (result) {
	  len = Array.getLength(value);
	  for (i = 0; i < len; i++) {
	    result = compareValues(Array.get(value, i), Array.get(getDefaultValue(), i));
	    if (!result)
	      break;
	  }
	}
      }
      else {
	result = compareValues(value, getDefaultValue());
      }
    }

    return result;
  }

  /**
   * Returns the current value of the option as string array.
   *
   * @return		the array representation of the current value
   */
  public String[] toArray() {
    List<String>	result;
    Object		currValue;
    Object		currValues;
    int			i;

    result = new ArrayList<String>();

    if (isVariableAttached()) {
      result.add("-" + getCommandline());
      result.add(getVariable());
    }
    else {
      currValue  = getCurrentValue();

      if (!isDefaultValue(currValue)) {
	currValues = null;

	if (currValue != null) {
	  if (!isMultiple()) {
	    currValues = Array.newInstance(getBaseClass(), 1);
	    Array.set(currValues, 0, currValue);
	  }
	  else {
	    currValues = currValue;
	  }

	  for (i = 0; i < Array.getLength(currValues); i++) {
	    result.add("-" + getCommandline());
	    result.add(toString(Array.get(currValues, i)));
	  }
	}
      }
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if ((getOwner() != null) && (getOwner().getVariables() != null))
      getOwner().getVariables().removeVariableChangeListener(this);
    m_BaseClass = null;

    super.cleanUp();
  }

  /**
   * Returns the commandline option and the property as string.
   *
   * @return			the commandline option and property
   */
  @Override
  public String toString() {
    return   "-" + getCommandline() + "/" + getProperty()
           + " <arg>" + (isMultiple() ? " ..." : "");
  }
}
