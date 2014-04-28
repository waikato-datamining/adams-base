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
 * ArrayConsumer.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import adams.core.Utils;
import adams.core.Variables;
import adams.core.logging.LoggingObject;

/**
 * Parses a string array of options. The element in the string array must
 * contain the classname of the object, the following ones are the actual
 * options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayConsumer
  extends AbstractRecursiveOptionConsumer<String[],String[]> {

  /** for serialization. */
  private static final long serialVersionUID = 3756322164593713820L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Processes the string array, commonly obtained from the command-line.";
  }

  /**
   * Creates the empty option handler from the internal data structure and
   * returns it. This option handler will then be "visited".
   *
   * @return		the generated option handler, null in case of an error
   */
  @Override
  protected OptionHandler initOutput() {
    OptionHandler	result;
    String		msg;

    try {
      result     = (OptionHandler) Class.forName(Conversion.rename(m_Input[0])).newInstance();
      m_Input[0] = "";
    }
    catch (Exception e) {
      msg = "Failed to initialize output:";
      logError(msg + " " + e);
      getLogger().log(Level.SEVERE, msg, e);
      result = null;
    }

    return result;
  }

  /**
   * Converts the input string into the internal format.
   *
   * @param s		the string to process
   * @return		the internal format, null in case of an error
   */
  @Override
  protected String[] convertToInput(String s) {
    String		msg;

    try {
      return OptionUtils.splitOptions(s);
    }
    catch (Exception e) {
      msg = "Failed to convert to input:";
      logError(msg + " " + e);
      getLogger().log(Level.SEVERE, msg, e);
      return null;
    }
  }

  /**
   * Processes the specified boolean option.
   *
   * @param option	the boolean option to process
   * @param values	ignored
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(BooleanOption option, String[] values) throws Exception {
    processOption((AbstractArgumentOption) option, values);
  }

  /**
   * Extracts the classname from the given commandline.
   *
   * @param cmdline	the commandline to process
   * @return		the classname
   */
  protected String extractClassname(String cmdline) {
    String	result;
    int		pos;

    pos = cmdline.indexOf(' ');
    if (pos > -1)
      result = cmdline.substring(0, pos);
    else
      result = cmdline;

    result = Conversion.rename(result);   // fix classname, if necessary

    return result;
  }

  /**
   * Processes the specified class option.
   *
   * @param option	the class option to process
   * @param values	the value(s) for the class option
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(ClassOption option, String[] values) throws Exception {
    Method			method;
    Object			object;
    Object			objects;
    ArrayConsumer		consumer;
    int				i;
    AbstractCommandLineHandler	handler;

    method = getWriteMethod(option);
    if (method == null)
      return;

    objects = Array.newInstance(option.getBaseClass(), values.length);

    for (i = 0; i < values.length; i++) {
      // variable?
      if (Variables.isPlaceholder(values[i])) {
	option.setVariable(values[i]);
	return;
      }

      object = Class.forName(extractClassname(values[i])).newInstance();  // we need to check actual instance of class, base class could be interface
      if (object instanceof OptionHandler) {
	consumer = new ArrayConsumer();
	consumer.setLoggingLevel(getLoggingLevel());
	object   = consumer.fromString(values[i]);
	m_Errors.addAll(consumer.getErrors());
	m_Warnings.addAll(consumer.getWarnings());
	consumer.cleanUp();
      }
      else {
	handler = AbstractCommandLineHandler.getHandler(object);
	object  = handler.fromCommandLine(values[i]);
      }

      Array.set(objects, i, object);

      checkDeprecation(object);

      if (!option.isMultiple())
	break;
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
   * @param values	the value(s) for the argument option
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(AbstractArgumentOption option, String[] values) throws Exception {
    Method	method;
    Object	objects;
    int		i;

    method = getWriteMethod(option);
    if (method == null)
      return;

    objects = Array.newInstance(option.getBaseClass(), values.length);

    for (i = 0; i < values.length; i++) {
      // variable?
      if (Variables.isPlaceholder(values[i])) {
	option.setVariable(values[i]);
	return;
      }

      Array.set(objects, i, option.valueOf(values[i]));
      if (!option.isMultiple())
	break;
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
   * <p/>
   * NB: collection is destructive, i.e., the flags and values are removed
   * from the array.
   *
   * @param option	the option to gather the arguments for
   * @param input	the command-line array to process
   * @return		the collected values
   */
  protected String[] collectValues(AbstractOption option, String[] input) {
    List<String>	result;
    int			i;
    String		optionStr;
    boolean		hasArg;
    boolean		isBool;

    result = new ArrayList<String>();

    hasArg    = (option instanceof AbstractArgumentOption);
    isBool    = (option instanceof BooleanOption);
    i         = 0;
    optionStr = getOptionIdentifier(option);
    while (i < input.length) {
      if (Conversion.renameOption(option.getOptionHandler().getClass().getName(), input[i]).equals(optionStr)) {
	input[i] = "";
	if (hasArg) {
	  if (isBool) {
	    if (i < input.length - 1) {
	      if (input[i + 1].equals("true") || input[i + 1].equals("false") || input[i + 1].startsWith(Variables.START)) {
		i++;
		result.add(input[i]);
		input[i] = "";
	      }
	      else {
		result.add("true");
	      }
	    }
	    else {
	      result.add("true");
	    }
	  }
	  else {
	    if (i < input.length - 1) {
	      i++;
	      result.add(input[i]);
	      input[i] = "";
	    }
	  }
	}
      }
      i++;
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Visits the options.
   *
   * @param manager	the manager to visit
   * @param input	the input data to use
   */
  @Override
  protected void doConsume(OptionManager manager, String[] input) {
    int			i;
    String		cmdline;
    AbstractOption	option;
    String[]		values;
    String		msg;

    i = 0;
    while (i < input.length) {
      cmdline = input[i];

      // skip empty strings
      if (cmdline.length() == 0) {
	i++;
	continue;
      }

      if (cmdline.startsWith("-")) {
	cmdline = cmdline.substring(1);
	cmdline = Conversion.renameOption(manager.getOwner().getClass().getName(), cmdline);
	option  = manager.findByFlag(cmdline);
	values  = null;
	if (option == null) {
	  msg = "Failed to find option (" + manager.getOwner().getClass().getName() + "): " + cmdline + "\n  --> Command-line: " + Utils.flatten(input, " ");
	  logWarning(msg);
	  getLogger().severe(msg);
	  // remove option
	  input[i] = "";
	  i++;
	  if (i < input.length) {
	    if (!input[i].startsWith("-"))
	      input[i] = "";
	  }
	}
	else {
	  if (option instanceof AbstractArgumentOption) {
	    values = collectValues(option, input);
	    if (values.length == 0) {
	      msg = "No argument supplied for option '" + option + "' (" + manager.getOwner().getClass().getName() + ")!";
	      logWarning(msg);
	      getLogger().severe(msg);
	    }
	  }
	  else if (option instanceof BooleanOption) {
	    // just remove the flag
	    collectValues(option, input);
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
      i++;
    }
  }

  /**
   * Sets the options of the option handler. Does check for remaining options.
   *
   * @param handler	the object to set the options for
   * @param options	the options to set
   */
  public static void setOptions(OptionHandler handler, String[] options) {
    setOptions(handler, options, true);
  }

  /**
   * Sets the options of the option handler.
   *
   * @param handler	the object to set the options for
   * @param options	the options to set
   * @param remaining	whether to check for remaining options
   */
  public static void setOptions(OptionHandler handler, String[] options, boolean remaining) {
    ArrayConsumer	consumer;
    String		msg;

    consumer = new ArrayConsumer();
    consumer.consume(handler, options);

    if (remaining) {
      msg = OptionUtils.checkRemainingOptions(options);
      if (msg != null) {
	if (handler instanceof LoggingObject)
	  ((LoggingObject) handler).getLogger().severe(msg);
	else
	  System.err.println(msg);
      }
    }
  }
}
