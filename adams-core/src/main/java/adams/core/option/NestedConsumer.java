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

import gnu.trove.list.array.TIntArrayList;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import adams.core.Utils;
import adams.core.Variables;
import adams.core.option.NestedFormatHelper.Line;

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
   * Collects all the line numbers.
   *
   * @param values	the list to traverse
   * @param range	for collecting the line numbers
   */
  protected void getLineRange(List values, TIntArrayList range) {
    for (Object obj: values) {
      if (obj instanceof Line)
	range.add(((Line) obj).getNumber());
      else if (obj instanceof ArrayList)
	getLineRange((List) obj, range);
    }
  }

  /**
   * Generates a line number range from the list of values
   * (mixed Line/ArrayList).
   *
   * @param values	the values to inspect
   * @return		the line range, null if no line numbers available
   */
  protected String getLineRange(List values) {
    TIntArrayList	range;
    int			min;
    int			max;

    range = new TIntArrayList();
    getLineRange(values, range);
    range.sort();
    if (range.size() > 0) {
      min = range.get(0);
      max = range.get(range.size() - 1);
      if (min == max)
	return "" + min;
      else
	return min + "-" + max;
    }
    else {
      return null;
    }
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
    Line		line;
    String		msg;

    try {
      line   = (Line) m_Input.get(0);
      result = (OptionHandler) Class.forName(Conversion.rename(line.getContent())).newInstance();
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
    int			offset;

    try {
      // split into separate lines
      lines  = new ArrayList<String>(Arrays.asList(s.split("\n")));
      offset = NestedFormatHelper.removeComments(lines);
      // convert into nested format
      return NestedFormatHelper.linesToNested(lines, offset);
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
    int				n;
    List			subset;
    List			optionsSet;
    AbstractCommandLineHandler	handler;
    String			msg;
    String			lines;
    Line			line;

    method = getWriteMethod(option);
    if (method == null)
      return;

    objects = Array.newInstance(option.getBaseClass(), values.size());
    lines   = getLineRange(values);

    for (i = 0; i < values.size(); i++) {
      if ((values.get(i).getClass() == Line.class) && Variables.isPlaceholder(((Line) values.get(i)).getContent())) {
	option.setVariable(((Line) values.get(i)).getContent());
	return;
      }

      subset = (List) values.get(i);
      line   = (Line) subset.get(0);
      line   = new Line(line.getNumber(), Conversion.rename(line.getContent()));  // fix classname, if necessary
      subset.set(0, line);
      object = Class.forName(((Line) subset.get(0)).getContent()).newInstance();  // we need to check actual instance of class, base class could be interface
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
	options = new String[optionsSet.size()];
	for (n = 0; n < optionsSet.size(); n++)
	  options[n] = ((Line) optionsSet.get(n)).getContent();
	handler = AbstractCommandLineHandler.getHandler(object);
	handler.setOptions(object, options);
      }

      Array.set(objects, i, object);

      checkDeprecation(object);

      if (!option.isMultiple())
	break;
    }

    try {
      if (!option.isMultiple())
	method.invoke(
	    option.getOptionHandler(),
	    new Object[]{Array.get(objects, 0)});
      else
	method.invoke(
	    option.getOptionHandler(),
	    new Object[]{objects});
    }
    catch (Exception e) {
      msg = "Failed to process class option '" + getOptionIdentifier(option) + "/" + option.getOptionHandler().getClass().getName() + "'" + (lines == null ? ": " : " (lines: " + lines + "):");
      logError(msg + "\n" + Utils.throwableToString(e));
      getLogger().log(Level.SEVERE, msg, e);
    }
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
    String	msg;
    String	lines;

    method = getWriteMethod(option);
    if (method == null)
      return;

    objects = Array.newInstance(option.getBaseClass(), values.size());
    lines   = getLineRange(values);

    for (i = 0; i < values.size(); i++) {
      // variable?
      if (Variables.isPlaceholder(((Line) values.get(i)).getContent())) {
	option.setVariable(((Line) values.get(i)).getContent());
	return;
      }

      Array.set(objects, i, option.valueOf(((Line) values.get(i)).getContent()));
      if (!option.isMultiple())
	break;
    }

    try {
      if (!option.isMultiple())
	method.invoke(
	    option.getOptionHandler(),
	    new Object[]{Array.get(objects, 0)});
      else
	method.invoke(
	    option.getOptionHandler(),
	    new Object[]{objects});
    }
    catch (Exception e) {
      msg = "Failed to process argument option '" + getOptionIdentifier(option) + "/" + option.getOptionHandler().getClass().getName() + "'" + (lines == null ? ": " : " (lines: " + lines + "):");
      logError(msg + "\n" + Utils.throwableToString(e));
      getLogger().log(Level.SEVERE, msg, e);
    }
  }

  /**
   * Collects all the arguments for given argument options.
   * <br><br>
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
      if (input.get(i).getClass() == Line.class) {
	if (Conversion.renameOption(option.getOptionHandler().getClass().getName(), ((Line) input.get(i)).getContent()).equals(optionStr)) {
	  input.remove(i);
	  if (hasArg) {
	    if (isBool) {
	      if (i < input.size()) {
		str = input.get(i).toString();
		if (str.equals("true") || str.equals("false") || str.startsWith(Variables.START)) {
		  result.add(new Line(str));
		  input.remove(i);
		}
		else {
		  result.add(new Line("true"));
		}
	      }
	      else {
		result.add(new Line("true"));
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
      if (!(input.get(i).getClass() == Line.class)) {
	i++;
	continue;
      }

      cmdline = ((Line) input.get(i)).getContent();

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
	  if ((i < input.size()) && (input.get(i).getClass() == Line.class)) {
	    if (!(((Line) input.get(i)).getContent()).startsWith("-"))
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
