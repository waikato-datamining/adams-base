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
 * ArrayProducer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generates the string array format that is used on the command-line.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayProducer
  extends AbstractRecursiveOptionProducer<String[],ArrayList<String>>
  implements RecursiveOptionProducer {

  /** for serialization. */
  private static final long serialVersionUID = 2014571979604068762L;

  /** the output vector. */
  protected ArrayList<String> m_OutputList;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a string array that can be used on the command-line.";
  }

  /**
   * Initializes the visitor.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_OutputList = new ArrayList<String>();
  }

  /**
   * Initializes the output data structure.
   *
   * @return		the created data structure
   */
  @Override
  protected String[] initOutput() {
    return new String[0];
  }

  /**
   * Visits a boolean option.
   *
   * @param option	the boolean option
   * @return		the last internal data structure that was generated
   */
  @Override
  public ArrayList<String> processOption(BooleanOption option) {
    return processOption((AbstractArgumentOption) option);
  }

  /**
   * Visits an argument option.
   *
   * @param option	the argument option
   * @return		the last internal data structure that was generated
   */
  @Override
  public ArrayList<String> processOption(AbstractArgumentOption option) {
    ArrayList<String>	result;
    Object		currValue;
    Object		currValues;
    int			i;

    result = new ArrayList<String>();

    if (option.isVariableAttached() && !m_OutputVariableValues) {
      result.add(getOptionIdentifier(option));
      result.add(option.getVariable());
    }
    else {
      currValue = getCurrentValue(option);

      if (!isDefaultValue(option, currValue)) {
	currValues = null;

	if (currValue != null) {
	  if (!option.isMultiple()) {
	    currValues = Array.newInstance(option.getBaseClass(), 1);
	    Array.set(currValues, 0, currValue);
	  }
	  else {
	    currValues = currValue;
	  }

	  for (i = 0; i < Array.getLength(currValues); i++) {
	    result.add(getOptionIdentifier(option));
	    result.add(option.toString(Array.get(currValues, i)));
	  }
	}
      }
    }

    if (m_Nesting.empty())
      m_OutputList.addAll(result);
    else
      ((ArrayList) m_Nesting.peek()).addAll(result);

    return result;
  }

  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		the last internal data structure that was generated
   */
  @Override
  public ArrayList<String> processOption(ClassOption option) {
    ArrayList<String>		result;
    Object			currValue;
    Object			currValues;
    int				i;
    Object			value;
    ArrayList<String>		nested;
    AbstractCommandLineHandler	handler;

    result = new ArrayList<String>();

    if (option.isVariableAttached() && !m_OutputVariableValues) {
      result.add(getOptionIdentifier(option));
      result.add(option.getVariable());
    }
    else {
      currValue = getCurrentValue(option);

      if (!isDefaultValue(option, currValue)) {
	currValues = null;

	if (currValue != null) {
	  if (!option.isMultiple()) {
	    currValues = Array.newInstance(option.getBaseClass(), 1);
	    Array.set(currValues, 0, currValue);
	  }
	  else {
	    currValues = currValue;
	  }

	  for (i = 0; i < Array.getLength(currValues); i++) {
	    value = Array.get(currValues, i);
	    result.add(getOptionIdentifier(option));
	    nested = new ArrayList<String>();
	    nested.add(value.getClass().getName());
	    if (value instanceof OptionHandler) {
	      m_Nesting.push(nested);
	      doProduce(((OptionHandler) value).getOptionManager());
	      m_Nesting.pop();
	    }
	    else {
	      handler = AbstractCommandLineHandler.getHandler(value);
	      nested.addAll(Arrays.asList(handler.getOptions(value)));
	    }
	    result.add(OptionUtils.joinOptions(nested.toArray(new String[nested.size()])));
	  }
	}
      }
    }

    if (result.size() > 0) {
      if (m_Nesting.empty())
	m_OutputList.addAll(result);
      else
	((ArrayList) m_Nesting.peek()).addAll(result);
    }

    return result;
  }

  /**
   * Hook-method before starting visiting options.
   */
  @Override
  protected void preProduce() {
    super.preProduce();

    m_Output = null;

    m_OutputList.clear();
    m_OutputList.add(m_Input.getClass().getName());
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output
   */
  @Override
  public String[] getOutput() {
    if (m_Output == null)
      m_Output = m_OutputList.toArray(new String[m_OutputList.size()]);

    return m_Output;
  }

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output, null in case of an error
   */
  @Override
  public String toString() {
    return OptionUtils.joinOptions(getOutput());
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_OutputList = null;
  }

  /**
   * Returns the string array with all the options of the handler, but without
   * the classname of the handler.
   *
   * @param handler	the handler to return the options for
   * @return		the options as flat string array
   */
  public static String[] getOptions(OptionHandler handler) {
    String[]		result;
    String[]		tmp;
    ArrayProducer	producer;

    producer = new ArrayProducer();
    producer.produce(handler);
    tmp    = producer.getOutput();
    result = new String[tmp.length - 1];
    System.arraycopy(tmp, 1, result, 0, tmp.length - 1);

    return result;
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(ArrayProducer.class, args);
  }
}
