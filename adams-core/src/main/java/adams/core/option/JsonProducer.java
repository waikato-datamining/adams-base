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
 * JsonProducer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.logging.Level;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import adams.core.io.FileFormatHandler;

/**
 * Generates the JSON format.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonProducer
  extends AbstractRecursiveOptionProducer<JSONObject,Object>
  implements FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = -7424639972010085977L;

  /** the key for the class name. */
  public final static String KEY_CLASS = "class";

  /** the key for the options. */
  public final static String KEY_OPTIONS = "options";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates JSON (JavaScript Object Notation) output.\n\n"
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
  protected JSONObject initOutput() {
    return new JSONObject();
  }
  
  /**
   * Adds the array.
   *
   * @param key		the identifier for the array
   * @param values	the values of the array
   */
  protected void addArray(JSONObject obj, String key, Object[] values) {
    JSONArray	array;

    try {
      array = new JSONArray();
      array.addAll(Arrays.asList(values));
      obj.put(key, array);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to add array as key '" + key + "':", e);
    }
  }

  /**
   * Adds the array.
   *
   * @param key		the identifier for the array
   * @param values	the values of the array
   */
  protected void addArray(String key, Object[] values) {
    JSONArray	array;

    array = new JSONArray();
    array.addAll(Arrays.asList(values));
    addPair(key, array);
  }

  /**
   * Adds the array.
   *
   * @param option	the associated option
   * @param key		the identifier for the array
   * @param values	the values of the array
   */
  protected void addArray(AbstractArgumentOption option, String key, Object values) {
    JSONArray	array;
    int		i;

    try {
      array = new JSONArray();
      for (i = 0; i < Array.getLength(values); i++) {
	if (!(option instanceof AbstractNumericOption) && !(option instanceof BooleanOption))
	  array.add(option.toString(Array.get(values, i)));
	else
	  array.add(Array.get(values, i));
      }
      addPair(key, array);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to add array for key '" + key + "':", e);
    }
  }

  /**
   * Adds the named key/value pair.
   *
   * @param key		the identifier for the value
   * @param value	the value
   */
  protected void addPair(String key, Object value) {
    Object	current;

    if (m_Nesting.empty())
      current = m_Output;
    else
      current = m_Nesting.peek();

    try {
      if (current instanceof JSONObject)
	((JSONObject) current).put(key, value);
      else if (current instanceof JSONArray)
	((JSONArray) current).add(value);
      else
	throw new IllegalStateException();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to add value for key '" + key + "':", e);
    }
  }

  /**
   * Creates a new JSON object.
   *
   * @param obj		the object to add to a JSONObject structure
   * @return		the JSON object
   */
  protected JSONObject newObject(Object obj) {
    JSONObject 	result;

    result = new JSONObject();
    try {
      result.put(KEY_CLASS, obj.getClass().getName());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to add class name: " + obj.getClass().getName(), e);
    }

    return result;
  }

  /**
   * Visits a boolean option.
   *
   * @param option	the boolean option
   * @return		always null
   */
  @Override
  public Object processOption(BooleanOption option) {
    Object	currValue;

    try {
      currValue = getCurrentValue(option);
      addPair(getOptionIdentifier(option), currValue);
    }
    catch (Exception e) {
      System.err.println("Error obtaining current value for '" + option.getProperty() + "':");
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		always null
   */
  @Override
  public Object processOption(ClassOption option) {
    Object			currValue;
    Object			currValues;
    Object			value;
    int				i;
    JSONArray			array;
    JSONObject			obj;
    AbstractCommandLineHandler	handler;

    if (option.isVariableAttached() && !m_OutputVariableValues) {
      addPair(getOptionIdentifier(option), option.getVariable());
    }
    else {
      currValue  = getCurrentValue(option);
      currValues = null;

      if (currValue != null) {
	if (!option.isMultiple()) {
	  value = currValue;
	  obj   = newObject(currValue);
	  addPair(getOptionIdentifier(option), obj);
	  if (value instanceof OptionHandler) {
	    m_Nesting.push(obj);
	    doProduce(((OptionHandler) value).getOptionManager());
	    m_Nesting.pop();
	  }
	  else {
	    handler = AbstractCommandLineHandler.getHandler(value);
	    addArray(obj, KEY_OPTIONS, handler.getOptions(value));
	  }
	}
	else {
	  currValues = currValue;
	  array = new JSONArray();
	  addPair(getOptionIdentifier(option), array);
	  m_Nesting.push(array);
	  for (i = 0; i < Array.getLength(currValues); i++) {
	    value = Array.get(currValues, i);
	    obj   = newObject(value);
	    array.add(obj);
	    if (value instanceof OptionHandler) {
	      m_Nesting.push(obj);
	      doProduce(((OptionHandler) value).getOptionManager());
	      m_Nesting.pop();
	    }
	    else {
	      handler = AbstractCommandLineHandler.getHandler(value);
	      addArray(obj, KEY_OPTIONS, handler.getOptions(value));
	    }
	  }
	  m_Nesting.pop();
	}
      }
    }

    return null;
  }

  /**
   * Visits an argument option.
   *
   * @param option	the argument option
   * @return		always null
   */
  @Override
  public Object processOption(AbstractArgumentOption option) {
    Object	currValue;

    if (option.isVariableAttached() && !m_OutputVariableValues) {
      addPair(getOptionIdentifier(option), option.getVariable());
    }
    else {
      currValue = getCurrentValue(option);
      if (currValue != null) {
	if (!option.isMultiple()) {
	  if (!(option instanceof AbstractNumericOption) && !(option instanceof BooleanOption))
	    currValue = option.toString(currValue);
	  addPair(getOptionIdentifier(option), currValue);
	}
	else {
	  addArray(option, getOptionIdentifier(option), currValue);
	}
      }
    }

    return null;
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output, null in case of an error
   */
  @Override
  public String toString() {
    StringWriter	writer;

    writer = new StringWriter();
    try {
      m_Output.writeJSONString(writer);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to generate string representation from JSON object:", e);
      writer = new StringWriter();
    }

    return writer.toString();
  }

  /**
   * Hook-method before starting visiting options.
   */
  @Override
  protected void preProduce() {
    super.preProduce();

    m_Output = newObject(m_Input);
  }

  /**
   * Returns the description of the file format.
   *
   * @return		the description
   */
  public String getFormatDescription() {
    return "JSON";
  }

  /**
   * Returns the default file extension (without the dot).
   *
   * @return		the default extension
   */
  public String getDefaultFormatExtension() {
    return "json";
  }

  /**
   * Returns the file extensions (without the dot).
   *
   * @return		the extensions
   */
  public String[] getFormatExtensions() {
    return new String[]{getDefaultFormatExtension()};
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(JsonProducer.class, args);
  }
}
