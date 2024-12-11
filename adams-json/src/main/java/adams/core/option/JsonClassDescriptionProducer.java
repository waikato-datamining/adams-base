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
 * JsonClassDescriptionProducer.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.Utils;
import adams.core.io.FileFormatHandler;
import adams.flow.control.Flow;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Generates a description of the class and its options in JSON format.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JsonClassDescriptionProducer
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
        "Generates a description of the class and its options in JSON format (JavaScript Object Notation).\n\n"
      + "For more information on JSON, see:\n"
      + "http://json.org/";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_UsePropertyNames = false;
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
      result.put(KEY_OPTIONS, new JSONArray());
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
    return processOption((AbstractArgumentOption) option);
  }

  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		always null
   */
  @Override
  public Object processOption(ClassOption option) {
    return processOption((AbstractArgumentOption) option);
  }

  /**
   * Visits an argument option.
   *
   * @param option	the argument option
   * @return		always null
   */
  @Override
  public Object processOption(AbstractArgumentOption option) {
    JSONObject	obj;
    Method help;

    obj = new JSONObject();
    obj.put("option", "-" + option.getCommandline());
    obj.put("property", option.getProperty());
    obj.put("type", Utils.classToString(option.getBaseClass()));
    obj.put("multiple", option.isMultiple());
    help = option.getToolTipMethod();
    if (help != null) {
      try {
	obj.put("help", help.invoke(option.getOwner().getOwner()));
      }
      catch (Exception e) {
	// ignored
      }
    }
    ((JSONArray) m_Output.get(KEY_OPTIONS)).add(obj);

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
    return "JSON (class)";
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
    // TODO
    // runProducer(JsonClassDescriptionProducer.class, args);
    JsonClassDescriptionProducer p = new JsonClassDescriptionProducer();
    JSONObject output = p.produce(new Flow());
    System.out.println("Output:\n" + output);
  }
}
