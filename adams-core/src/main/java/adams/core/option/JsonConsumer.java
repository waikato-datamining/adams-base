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
 * JsonConsumer.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.Utils;
import adams.core.Variables;
import adams.core.io.FileFormatHandler;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Recreates objects from a JSON representation.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonConsumer
  extends AbstractRecursiveOptionConsumer<JSONObject,Object>
  implements FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = -840227436726154503L;

  /** the key for the class name. */
  public final static String KEY_CLASS = JsonProducer.KEY_CLASS;

  /** the key for the options. */
  public final static String KEY_OPTIONS = JsonProducer.KEY_OPTIONS;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Processes JSON (JavaScript Object Notation) input.\n\n"
      + "For more information on JSON, see:\n"
      + "http://json.org/";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_UsePropertyNames = true;
  }

  /**
   * Initializes the output data structure.
   *
   * @return		the created data structure
   */
  @Override
  protected OptionHandler initOutput() {
    OptionHandler	result;
    String		msg;

    result = null;

    if (m_Input.containsKey(KEY_CLASS)) {
      try {
	result = (OptionHandler) Class.forName(Conversion.getSingleton().rename((String) m_Input.get(KEY_CLASS))).newInstance();
      }
      catch (Exception e) {
	msg = "Failed to instantiate class: ";
	getLogger().log(Level.SEVERE, msg, e);
	logError(msg + e);
      }
    }
    else {
      logError("Key '" + KEY_CLASS + "' not present!");
    }

    return result;
  }

  /**
   * Processes the specified boolean option.
   *
   * @param option	the boolean option to process
   * @param values	the value for the boolean option
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(BooleanOption option, Object values) throws Exception {
    Method	method;
    Boolean	param;
    String	msg;

    method = getWriteMethod(option);
    if (method == null)
      return;

    if (Array.getLength(values) != 1) {
      msg = "No value available for option '" + option.getProperty() + "'?";
      getLogger().log(Level.SEVERE, msg);
      logError(msg);
      return;
    }

    param = (Boolean) Array.get(values, 0);

    method.invoke(
	option.getOptionHandler(),
	new Object[]{param});
  }

  /**
   * Processes the specified class option.
   *
   * @param option	the class option to process
   * @param values	the value for the class option
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(ClassOption option, Object values) throws Exception {
    Method		method;
    String		msg;
    JSONObject		object;
    JSONArray		array;
    Object		value;
    Object		objects;
    int			i;
    Object		obj;
    OptionHandler	handler;
    String[]		strOptions;

    method = getWriteMethod(option);
    if (method == null)
      return;

    if (Array.getLength(values) != 1) {
      msg = "No value available for option '" + option.getProperty() + "'?";
      getLogger().log(Level.SEVERE, msg);
      logError(msg);
      return;
    }

    value = Array.get(values, 0);

    // variable?
    if ((value instanceof String) && Variables.isPlaceholder((String) value)) {
      option.setVariable((String) value);
      return;
    }

    if (value instanceof JSONObject) {
      object  = (JSONObject) value;
      objects = Array.newInstance(option.getBaseClass(), 1);
      obj     = Class.forName((String) object.get(KEY_CLASS)).newInstance();
      if (obj instanceof OptionHandler) {
	handler = (OptionHandler) obj;
	Array.set(objects, 0, handler);
	checkDeprecation(handler);
	doConsume(handler.getOptionManager(), object);
      }
      else {
	strOptions = (String[]) ((JSONArray) object.get(KEY_OPTIONS)).toArray(new String[0]);
	obj        = OptionUtils.forName(Object.class, obj.getClass().getName(), strOptions);
	Array.set(objects, 0, obj);
      }
    }
    else if (value instanceof JSONArray) {
      array   = (JSONArray) value;
      objects = Array.newInstance(option.getBaseClass(), array.size());
      for (i = 0; i < array.size(); i++) {
	object = (JSONObject) array.get(i);
	obj    = Class.forName((String) object.get(KEY_CLASS)).newInstance();
	if (obj instanceof OptionHandler) {
	  handler = (OptionHandler) obj;
	  Array.set(objects, i, handler);
	  checkDeprecation(handler);
	  doConsume(handler.getOptionManager(), object);
	}
	else {
	  strOptions = (String[]) ((JSONArray) object.get(KEY_OPTIONS)).toArray(new String[0]);
	  obj        = OptionUtils.forName(Object.class, obj.getClass().getName(), strOptions);
	  Array.set(objects, i, obj);
	}
      }
    }
    else {
      msg = "Unhandled JSON type: " + value.getClass().getName();
      getLogger().log(Level.SEVERE, msg);
      logError(msg);
      return;
    }

    if (!option.isMultiple())
      method.invoke(
	  option.getOptionHandler(),
	  new Object[]{Array.get(objects, 0)});
    else
      method.invoke(
	  option.getOptionHandler(),
	  new Object[]{objects});
  }

  /**
   * Processes the specified argument option.
   *
   * @param option	the argument option to process
   * @param values	the value for the argument option
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(AbstractArgumentOption option, Object values) throws Exception {
    Method	method;
    Object	objects;
    int		i;
    Object	value;
    JSONArray	array;
    String	msg;

    method = getWriteMethod(option);
    if (method == null)
      return;

    if (Array.getLength(values) != 1) {
      msg = "No value available for option '" + option.getProperty() + "'?";
      getLogger().log(Level.SEVERE, msg);
      logError(msg);
      return;
    }

    value = Array.get(values, 0);

    // variable?
    if ((value instanceof String) && Variables.isPlaceholder((String) value)) {
      option.setVariable((String) value);
      return;
    }

    if (value instanceof JSONArray) {
      array   = (JSONArray) value;
      objects = Array.newInstance(option.getBaseClass(), array.size());
      for (i = 0; i < array.size(); i++)
	Array.set(objects, i, option.valueOf(array.get(i).toString()));
    }
    else {
      objects = Array.newInstance(option.getBaseClass(), 1);
      Array.set(objects, 0, option.valueOf(value.toString()));
    }

    if (!option.isMultiple())
      method.invoke(
	  option.getOptionHandler(),
	  new Object[]{Array.get(objects, 0)});
    else
      method.invoke(
	  option.getOptionHandler(),
	  new Object[]{objects});
  }

  /**
   * Collects all the arguments for given argument options.
   *
   * @param option	the option to gather the arguments for
   * @param input	the command-line array to process
   * @return		the collected values
   */
  protected Object[] collectValues(AbstractOption option, JSONObject input) {
    List	result;
    Iterator	iter;
    String	optionStr;
    String	name;

    result = new ArrayList();

    optionStr = getOptionIdentifier(option);
    iter      = input.keySet().iterator();
    while (iter.hasNext()) {
      name = (String) iter.next();
      if (Conversion.getSingleton().renameOption(option.getOptionHandler().getClass().getName(), name).equals(optionStr))
	result.add(input.get(name));
    }

    return result.toArray(new Object[result.size()]);
  }

  /**
   * Visits the options.
   *
   * @param manager	the manager to visit
   * @param input	the input data to use
   */
  @Override
  protected void doConsume(OptionManager manager, JSONObject input) {
    Iterator		iter;
    AbstractOption	option;
    String		name;
    String		msg;
    Object[]		values;

    iter = input.keySet().iterator();
    while (iter.hasNext()) {
      name   = (String) iter.next();
      if (name.equals(KEY_CLASS) || name.equals(KEY_OPTIONS))
	continue;
      name   = Conversion.getSingleton().renameOption(manager.getOwner().getClass().getName(), name);
      option = manager.findByProperty(name);

      if (option == null) {
	msg = "Failed to find option (" + manager.getOwner().getClass().getName() + "): " + name;
	logWarning(msg);
	getLogger().log(Level.SEVERE, msg);
      }
      else {
	values = collectValues(option, input);
	if (option instanceof AbstractArgumentOption) {
	  if (values.length == 0) {
	    msg = "No argument supplied for option '" + option + "' (" + manager.getOwner().getClass().getName() + ")!";
	    logWarning(msg);
	    getLogger().log(Level.SEVERE, msg);
	  }
	}

	try {
	  processOption(option, values);
	}
	catch (Exception e) {
	  msg = "Failed to process option '" + getOptionIdentifier(option) + "/" + manager.getOwner().getClass().getName() + "':";
	  logError(msg + " " + Utils.throwableToString(e));
	  getLogger().log(Level.SEVERE, msg, e);
	}
      }
    }
  }

  /**
   * Converts the input string into the internal format.
   *
   * @param s		the string to process
     * @return		the internal format, null in case of an error
   */
  @Override
  protected JSONObject convertToInput(String s) {
    JSONObject	result;
    JSONParser	tokener;
    String	msg;

    result  = new JSONObject();

    try {
      tokener = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
      result  = (JSONObject) tokener.parse(s);
    }
    catch (Exception e) {
      msg = "Failed to parse JSON string: ";
      getLogger().log(Level.SEVERE, msg, e);
      logError(msg + e);
    }

    return result;
  }

  /**
   * Returns the description of the file format.
   *
   * @return		the description
   */
  @Override
  public String getFormatDescription() {
    return "JSON";
  }

  /**
   * Returns the default file extension (without the dot).
   *
   * @return		the default extension
   */
  @Override
  public String getDefaultFormatExtension() {
    return "json";
  }

  /**
   * Returns the file extensions (without the dot).
   *
   * @return		the extensions
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{getDefaultFormatExtension()};
  }
}
