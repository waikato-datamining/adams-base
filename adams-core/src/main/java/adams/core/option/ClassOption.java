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
 * ClassOption.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import nz.ac.waikato.cms.locator.ClassLocator;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Option class for OptionHandler options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassOption
  extends AbstractArgumentOption {

  /** for serialization. */
  private static final long serialVersionUID = 945814491797364391L;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected ClassOption(OptionManager owner, String commandline, String property,
      Object defValue) {

    this(owner, commandline, property, defValue, true);
  }

  /**
   * Initializes the option.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   * @param outputDefValue	whether to output the default value or not
   */
  protected ClassOption(OptionManager owner, String commandline, String property,
      Object defValue, boolean outputDefValue) {

    super(owner, commandline, property, defValue, outputDefValue);
  }

  /**
   * Skips the test, as it would be too expensive.
   *
   * @param value	the value to compare against the default value
   * @param defValue	the default value to compare against
   * @return		always false
   */
  @Override
  protected boolean compareValues(Object value, Object defValue) {
    return false;
  }

  /**
   * Turns the string into the appropriate object.
   *
   * @param s		the string to parse
   * @return		the generated object
   * @throws Exception	if parsing of string fails
   */
  @Override
  public Object valueOf(String s) throws Exception {
    return OptionUtils.forAnyCommandLine(getBaseClass(), s);
  }

  /**
   * Returns a string representation of the specified object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toString(Object obj) {
    return OptionUtils.getCommandLine(obj);
  }

  /**
   * Sets the values obtained from the nested structure.
   *
   * @param values	the values to set
   * @see		#fromNested(ArrayList)
   */
  protected void setNestedValues(ArrayList values) {
    String			classname;
    ArrayList			pair;
    Object			value;
    Object			element;
    int				size;
    int				i;
    int				n;
    String			str;
    String			optionStr;
    Method			method;
    NestedConsumer 		consumer;
    ArrayList			nested;
    ArrayList<String>		options;
    AbstractCommandLineHandler	handler;

    if (isMultiple())
      size = values.size();
    else
      size = 1;
    value = Array.newInstance(getBaseClass(), size);

    if (getDebug())
      System.out.println(
	  getOptionHandler().getClass().getName() + "/"
	  + getClass().getName() + "/"
	  + "-" + getCommandline() + ": #" + size + " value(s)");

    try {
      for (i = 0; i < size; i++) {
	// in case the option handler didn't have any options it won't be
	// stored as ArrayList, but as plain string. also, Weka options
	// could be stored as single string as well
	if (values.get(i) instanceof String) {
	  pair = new ArrayList();
	  str  = (String) values.get(i);
	  if (str.indexOf(' ') > -1) {
	    pair.add(str.substring(0, str.indexOf(' ')));
	    pair.add(str.substring(str.indexOf(' ') + 1));
	    // the option string will be processed later, since we have to
	    // instantiate the object first in order to determine what
	    // type of option handler it is - the property might only list
	    // an interface!
	  }
	  else {
	    pair.add(str);
	  }
	}
	else {
	  pair = (ArrayList) values.get(i);
	}
	classname = (String) pair.get(0);
	element   = OptionUtils.forName(getBaseClass(), classname, new String[0]);
	Array.set(value, i, element);

	// any options for the option handler?
	if (pair.size() > 1) {
	  // do we still need to split the options?
	  if (pair.get(1) instanceof String) {
	    optionStr = (String) pair.get(1);
	    if (ClassLocator.hasInterface(OptionHandler.class, element.getClass())) {
	      pair.set(1, new ArrayList(Arrays.asList(OptionUtils.splitOptions(optionStr))));
	    }
	    else {
	      handler = AbstractCommandLineHandler.getHandler(element);
	      pair.set(1, new ArrayList(Arrays.asList(handler.splitOptions(optionStr))));
	    }
	  }

	  // set options
	  if (ClassLocator.hasInterface(OptionHandler.class, element.getClass())) {
	    consumer = new NestedConsumer();
	    consumer.consume((OptionHandler) element, (ArrayList) pair.get(1));
	    consumer.cleanUp();
	  }
	  else {
	    handler = AbstractCommandLineHandler.getHandler(element);
	    nested  = (ArrayList) pair.get(1);
	    options = new ArrayList<String>();
	    for (n = 0; n < nested.size(); n++)
	      options.add((String) nested.get(n));
	    handler.setOptions(element, options.toArray(new String[options.size()]));
	  }
	}
      }

      method = getWriteMethod();
      if (isMultiple())
	method.invoke(getOptionHandler(), value);
      else
	method.invoke(getOptionHandler(), Array.get(value, 0));
    }
    catch (Exception e) {
      if (!m_Owner.isQuiet()) {
	System.err.println(
	    "Error setting nested values for "
		+ getOptionHandler().getClass().getName() + "/"
		+ getClass().getName() + "/"
		+ "-" + getCommandline() + ": " + values);
	e.printStackTrace();
      }
      return;
    }
  }

  /**
   * Adds additional information about the argument, e.g., the class.
   *
   * @param buffer	the buffer to add the information to
   */
  protected void addArgumentInfo(StringBuilder buffer) {
    buffer.append(" <" + getBaseClass().getName() + " [options]>");
  }

  /**
   * Returns the commandline option and the property as string.
   *
   * @return			the commandline option and property
   */
  @Override
  public String toString() {
    return   "-" + getCommandline() + "/" + getProperty()
           + " <" + m_BaseClass.getName() + ">" + (isMultiple() ? " ..." : "");
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    Object	value;
    Object	values;
    int		i;
    int		len;

    value = getCurrentValue();
    if (!isMultiple()) {
      values = Array.newInstance(getBaseClass(), 1);
      Array.set(values, 0, value);
    }
    else {
      values = value;
      len    = Array.getLength(values);
    }

    len = Array.getLength(values);
    for (i = 0; i < len; i++) {
      value = Array.get(values, i);
      if (value instanceof OptionHandler) {
	try {
	  ((OptionHandler) value).cleanUpOptions();
	}
	catch (Exception e) {
	  if (!m_Owner.isQuiet()) {
	    System.err.println("Error calling cleanUp()/" + getProperty() + ":");
	    e.printStackTrace();
	  }
	}
      }
    }

    super.cleanUp();
  }
}
