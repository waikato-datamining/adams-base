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
 * NestedConsumer.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import adams.core.Utils;
import adams.core.Variables;

/**
 * Parses a nested ArrayList of options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NestedConsumer
  extends AbstractRecursiveOptionConsumer<List,List> {

  /** for serialization. */
  private static final long serialVersionUID = 3076988578982973033L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Processes the nested format (tab indentation in string representation, nested ArrayList objects in object representation).";
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
      result = (OptionHandler) Class.forName(Conversion.rename((String) m_Input.get(0))).newInstance();
      m_Input.remove(0);
      if (m_Input.size() > 0) {
	if (m_Input.get(0) instanceof ArrayList)
	  m_Input = (List) m_Input.get(0);
	else
	  m_Input = new ArrayList();
      }
      else {
	m_Input = new ArrayList();
      }
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
  protected List convertToInput(String s) {
    ArrayList<String>	lines;
    String		msg;

    try {
      // split into separate lines
      lines = new ArrayList<String>(Arrays.asList(s.split("\n")));
      // skip comments
      while ((lines.size() > 0) && (lines.get(0).startsWith(NestedProducer.COMMENT)))
	lines.remove(0);
      // convert into nested format
      return NestedFormatHelper.linesToNested(lines);
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
  protected void processOption(BooleanOption option, List values) throws Exception {
    doProcessOption(option, values);
  }

  /**
   * Processes the specified class option.
   *
   * @param option	the class option to process
   * @param values	the value(s) for the class option
   * @throws Exception	if something goes wrong
   */
  @Override
  protected void processOption(ClassOption option, List values) throws Exception {
    String[]			options;
    Method			method;
    Object			object;
    Object			objects;
    NestedConsumer		consumer;
    int				i;
    List			subset;
    List			optionsSet;
    AbstractCommandLineHandler	handler;

    method = getWriteMethod(option);
    if (method == null)
      return;

    objects = Array.newInstance(option.getBaseClass(), values.size());

    for (i = 0; i < values.size(); i++) {
      if ((values.get(i).getClass() == String.class) && Variables.isPlaceholder((String) values.get(i))) {
	option.setVariable((String) values.get(i));
	return;
      }

      subset = (List) values.get(i);
      subset.set(0, Conversion.rename((String) subset.get(0)));  // fix classname, if necessary
      object = Class.forName((String) subset.get(0)).newInstance();  // we need to check actual instance of class, base class could be interface
      if (object instanceof OptionHandler) {
	consumer = new NestedConsumer();
	consumer.setLoggingLevel(getLoggingLevel());
	consumer.setInput(subset);
	object   = consumer.consume();
	m_Errors.addAll(consumer.getErrors());
	m_Warnings.addAll(consumer.getWarnings());
	consumer.cleanUp();
      }
      else {
	if (subset.size() > 1)
	  optionsSet = (List) subset.get(1);
	else
	  optionsSet = new ArrayList();
	options = (String[]) optionsSet.toArray(new String[optionsSet.size()]);
	handler = AbstractCommandLineHandler.getHandler(object);
	handler.setOptions(object, options);
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
  protected void processOption(AbstractArgumentOption option, List values) throws Exception {
    doProcessOption(option, values);
  }

  /**
   * Processes the specified argument option.
   *
   * @param option	the argument option to process
   * @param values	the value(s) for the argument option
   * @throws Exception	if something goes wrong
   */
  protected void doProcessOption(AbstractArgumentOption option, List values) throws Exception {
    Method	method;
    Object	objects;
    int		i;

    method = getWriteMethod(option);
    if (method == null)
      return;

    objects = Array.newInstance(option.getBaseClass(), values.size());

    for (i = 0; i < values.size(); i++) {
      // variable?
      if (Variables.isPlaceholder((String) values.get(i))) {
	option.setVariable((String) values.get(i));
	return;
      }

      Array.set(objects, i, option.valueOf((String) values.get(i)));
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
   * from the vector.
   *
   * @param option	the option to gather the arguments for
   * @param input	the command-line array to process
   * @return		the collected values
   */
  protected ArrayList collectValues(AbstractOption option, List input) {
    ArrayList	result;
    int		i;
    String	optionStr;
    boolean	hasArg;
    boolean	isBool;
    String	str;

    result = new ArrayList();
    
    hasArg    = (option instanceof AbstractArgumentOption);
    isBool    = (option instanceof BooleanOption);
    i         = 0;
    optionStr = getOptionIdentifier(option);
    while (i < input.size()) {
      if (input.get(i).getClass() == String.class) {
	if (Conversion.renameOption(option.getOptionHandler().getClass().getName(), (String) input.get(i)).equals(optionStr)) {
	  input.remove(i);
	  if (hasArg) {
	    if (isBool) {
	      if (i < input.size()) {
		str = input.get(i).toString();
		if (str.equals("true") || str.equals("false") || str.startsWith(Variables.START)) {
		  result.add(str);
		  input.remove(i);
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
	      if (i < input.size()) {
		result.add(input.get(i));
		input.remove(i);
	      }
	    }
	  }
	}
	else {
	  i++;
	}
      }
      else {
	i++;
      }
    }

    return result;
  }

  /**
   * Visits the options.
   *
   * @param manager	the manager to visit
   * @param input	the input data to use
   */
  @Override
  protected void doConsume(OptionManager manager, List input) {
    int			i;
    String		cmdline;
    AbstractOption	option;
    ArrayList		values;
    String		msg;

    i = 0;
    while (i < input.size()) {
      if (!(input.get(i).getClass() == String.class)) {
	i++;
	continue;
      }

      cmdline = (String) input.get(i);

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
	  // remove unknown option
	  input.remove(i);
	  if ((i < input.size()) && (input.get(i).getClass() == String.class)) {
	    if (!((String) input.get(i)).startsWith("-"))
	      input.remove(i);
	  }
	  else {
	    if (i < input.size())
	      input.remove(i);
	  }
	}
	else {
	  if (option instanceof AbstractArgumentOption) {
	    values = collectValues(option, input);
	    if (values.size() == 0) {
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
    }
  }
}
